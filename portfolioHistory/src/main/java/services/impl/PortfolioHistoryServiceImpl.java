package services.impl;

import models.Currency;
import models.*;
import models.entities.IntradayMovementsEntity;
import models.entities.PortfolioHistoryEntity;
import models.entities.PortfolioPrivacyInfoEntity;
import models.enums.DurationIntervalEnum;
import models.enums.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import repositories.IntradayMovementsRepository;
import repositories.PortfolioHistoryRepository;
import repositories.PortfolioInfoRepository;
import services.PortfolioHistoryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioHistoryServiceImpl implements PortfolioHistoryService {

    @Autowired
    private PortfolioHistoryRepository repository;

    @Autowired
    private IntradayMovementsRepository intradayMovementsRepository;

    @Autowired
    private PortfolioInfoRepository portfolioInfoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * Method to update a portfolio, this method is called each time there is a new transaction in the portfolio history, the input will be from RabbitMQ
     */
    @Override
    public void insertIntradayMovement(MovementBin movementBin) {
        IntradayMovementsEntity entity = new IntradayMovementsEntity();
        entity.setPortfolioId(movementBin.getPortfolioId());
        entity.setCurrency(movementBin.getCurrency());
        entity.setPrice(movementBin.getPrice());
        entity.setDate(movementBin.getDate());
        entity.setAmount(movementBin.getAmount());
        entity.setType(movementBin.getType());
        entity.setSymbolId(movementBin.getSymbolId());
        intradayMovementsRepository.save(entity);
    }

    /**
     * Method to update old movement, this means that starting from the initial modifying date all the portfolio records will be modified
     *
     * @param movementBin
     */
    @Override
    public void updateOldMovements(MovementBin movementBin) {
        //STEP1 retrieve initial date
        LocalDate startDate = movementBin.getDate();
        //step2 call the API to get all the transaction starting from initial date
        List<ServiceInstance> instances = discoveryClient.getInstances("transaction");
        if (instances != null && !instances.isEmpty()) {
            // Get a random instance
            String url = instances.get(new Random().nextInt(instances.size())).getUri().toString();
            RestTemplate restTemplate = new RestTemplate();
            try {
                ResponseEntity<List<MovementBin>> responseEntity = restTemplate.exchange(
                        url + "/api/v1/transaction/get-by-portfolio/" + movementBin.getPortfolioId() + "/after-date?date=" + startDate.toString(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<MovementBin>>() {
                        });

                List<MovementBin> movements = Optional.ofNullable(responseEntity.getBody()).orElse(List.of());

                List<ServiceInstance> assetAllocationService = discoveryClient.getInstances("transaction");
                List<GetAssetQuantityBin> assetAllocation = null;
                if (assetAllocationService != null && !assetAllocationService.isEmpty()) {
                    // Get a random instance
                    String assetAllocationUrl = assetAllocationService.get(new Random().nextInt(instances.size())).getUri().toString();

                    ResponseEntity<List<GetAssetQuantityBin>> assetResponseEntityList = restTemplate.exchange(
                            assetAllocationUrl + "/api/v1/transaction/get-by-portfolio/" + movementBin.getPortfolioId() +
                                    "/assets-qty?date=" + movementBin.getDate(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<GetAssetQuantityBin>>() {
                            });
                    assetAllocation = assetResponseEntityList.getBody();
                }
                assetAllocation = Optional.ofNullable(assetAllocation).orElse(new ArrayList<>());

                List<ServiceInstance> assets = discoveryClient.getInstances("asset");
                //This map will contain all the information regarding asset price in the selected period
                Map<String, Asset> assetMap = new HashMap<>();
                Map<String, Currency> currenciesMap = new HashMap<>();
                if (assets != null && !assets.isEmpty()) {
                    // Get a random instance
                    String assetUrl = assets.get(new Random().nextInt(instances.size())).getUri().toString();
                    // this set will contain all the asset in a portfolio to get the evaluation day by day
                    Set<String> assetsInPortfolio = movements.stream().map(MovementBin::getSymbolId).collect(Collectors.toSet());

                    assetsInPortfolio.addAll(assetAllocation.stream().map(GetAssetQuantityBin::getSymbolId).collect(Collectors.toSet()));

                    //step3 call asset to get data for each asset
                    assetsInPortfolio.forEach(asset -> {
                        if (!assetMap.containsKey(asset)) {
                            ResponseEntity<Asset> assetResponseEntity = restTemplate.exchange(
                                    assetUrl + "/api/v1/asset/" + asset + "?startDate=" + movementBin.getDate(),
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<Asset>() {
                                    });
                            assetMap.put(asset, assetResponseEntity.getBody());
                        }
                    });

                    Set<String> currencies = assetMap.values().stream().map(Asset::getCurrency).collect(Collectors.toSet());
                    currencies.forEach(currency -> {
                        if (!currenciesMap.containsKey(currency)) {
                            ResponseEntity<Currency> currencyResponseEntity = restTemplate.exchange(
                                    assetUrl + "/api/v1/currency/" + currency + "/EUR",
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<Currency>() {
                                    });
                            currenciesMap.put(currency, currencyResponseEntity.getBody());
                        }
                    });
                }
                LocalDate currentDate = startDate;
                List<LocalDate> dates = new ArrayList<>();
                while (currentDate.isBefore(LocalDate.now())) {
                    dates.add(currentDate);
                    currentDate = currentDate.plusDays(1);
                }
                //create a map to store all the infos required to calculate the portfolio value (store date-[asset-price])
                Map<LocalDate, List<Pair<String, BigDecimal>>> assetDayByDay = new HashMap<>();
                Map<LocalDate, List<Pair<String, BigDecimal>>> currenciesDayByDay = new HashMap<>();
                Map<LocalDate, List<GetAssetQuantityBin>> portfolioDayByDay = new HashMap<>();
                portfolioDayByDay.put(movementBin.getDate(), assetAllocation);
                List<GetAssetQuantityBin> finalAssetAllocation = assetAllocation;
                dates.forEach(date -> {
                    List<Pair<String, BigDecimal>> assetList = new ArrayList<>();
                    List<Pair<String, BigDecimal>> currenciesList = new ArrayList<>();
                    assetMap.keySet().forEach(assetName -> {
                        int index = assetMap.get(assetName).getDates().indexOf(date);
                        BigDecimal price = BigDecimal.ONE;
                        if (index != -1) {
                            price = assetMap.get(assetName).getPrices().get(index);
                        } else {
                            if (assetDayByDay.containsKey(date.minusDays(1))) {
                                List<Pair<String, BigDecimal>> pairList = assetDayByDay.get(date.minusDays(1));
                                for (Pair<String, BigDecimal> pair : pairList) {
                                    if (pair.getFirst().equals(assetName)) {
                                        price = pair.getSecond();
                                    }
                                }
                            }
                        }
                        assetList.add(Pair.of(assetName, price));
                    });
                    currenciesMap.keySet().forEach(currency -> {
                        int index = currenciesMap.get(currency).getDateList().indexOf(date);
                        BigDecimal price = BigDecimal.ONE;
                        if (index != -1) {
                            price = currenciesMap.get(currency).getPriceList().get(index);
                        } else {
                            if (currenciesDayByDay.containsKey(date.minusDays(1))) {
                                List<Pair<String, BigDecimal>> pairList = currenciesDayByDay.get(date.minusDays(1));
                                for (Pair<String, BigDecimal> pair : pairList) {
                                    if (pair.getFirst().equals(currency)) {
                                        price = pair.getSecond();
                                    }
                                }
                            }
                        }
                        currenciesList.add(Pair.of(currency, price));
                    });
                    assetDayByDay.put(date, assetList);
                    currenciesDayByDay.put(date, currenciesList);

                    // This part is used to calculate the asset allocation day by day for all the time that needs to be updated
                    List<GetAssetQuantityBin> dailyAssetAllocation = Optional.ofNullable(portfolioDayByDay.get(date.minusDays(1))).orElse(List.of());
                    List<MovementBin> dailyMovements = movements.stream().filter(e -> date.equals(e.getDate())).toList();
                    List<BigDecimal> intradayMovementTotal = new ArrayList<>(List.of(BigDecimal.ZERO));
                    List<BigDecimal> removedMoney = new ArrayList<>(List.of(BigDecimal.ZERO));
                    List<BigDecimal> portfolioValue = new ArrayList<>(List.of(BigDecimal.ZERO));
                    dailyMovements.forEach(dailyMovement -> {
                        if (!dailyAssetAllocation.stream().filter(e -> e.getSymbolId().equals(dailyMovement.getSymbolId())).toList().isEmpty()) {
                            int index = -1;
                            for (int i = 0; i < dailyAssetAllocation.size(); i++) {
                                if (dailyAssetAllocation.get(i).getSymbolId().equals(dailyMovement.getSymbolId())) {
                                    index = i;
                                    break;
                                }
                            }

                            if (TransactionTypeEnum.BUY.equals(dailyMovement.getType())) {
                                dailyAssetAllocation.set(index,
                                        new GetAssetQuantityBin(dailyAssetAllocation.get(index).getSymbolId(),
                                                dailyAssetAllocation.get(index).getAmount() + dailyMovement.getAmount()));
                            } else {
                                dailyAssetAllocation.set(index,
                                        new GetAssetQuantityBin(dailyAssetAllocation.get(index).getSymbolId(),
                                                dailyAssetAllocation.get(index).getAmount() - dailyMovement.getAmount()));
                            }

                        } else {
                            dailyAssetAllocation.add(new GetAssetQuantityBin(dailyMovement.getSymbolId(), dailyMovement.getAmount()));
                        }


                        if (TransactionTypeEnum.BUY.equals(dailyMovement.getType())) {
                            //case BUY
                            Optional<GetAssetQuantityBin> asset = finalAssetAllocation.stream().filter(e -> dailyMovement.getSymbolId().equals(e.getSymbolId())).findFirst();
                            if (asset.isPresent()) {
                                finalAssetAllocation.get(finalAssetAllocation.indexOf(asset.get())).setAmount(asset.get().getAmount() + dailyMovement.getAmount());
                            } else {
                                GetAssetQuantityBin newAsset = GetAssetQuantityBin.builder().symbolId(dailyMovement.getSymbolId()).amount(dailyMovement.getAmount()).build();
                                finalAssetAllocation.add(newAsset);
                            }
                            if (!"EUR".equals(dailyMovement.getCurrency())) {
                                //asset price not in EUR, make the coversion before adding to the list
                                intradayMovementTotal.set(0, intradayMovementTotal.get(0).add(dailyMovement.getPrice()).multiply(BigDecimal.valueOf(dailyMovement.getAmount()))
                                        .multiply(currenciesDayByDay.get(date).stream().filter(x -> x.getFirst().equals(dailyMovement.getCurrency())).findFirst().orElseThrow().getSecond()));

                            } else {
                                //asset price in EUR
                                intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(dailyMovement.getPrice().multiply(BigDecimal.valueOf(dailyMovement.getAmount()))));
                            }
                        } else {
                            //case SELL
                            Optional<GetAssetQuantityBin> asset = finalAssetAllocation.stream().filter(e -> dailyMovement.getSymbolId().equals(e.getSymbolId())).findFirst();
                            if (asset.isPresent()) {
                                if ((asset.get().getAmount() - dailyMovement.getAmount()) >= 0) {
                                    finalAssetAllocation.get(finalAssetAllocation.indexOf(asset.get())).setAmount(asset.get().getAmount() - dailyMovement.getAmount());
                                    BigDecimal amount;
                                    if (!"EUR".equals(dailyMovement.getCurrency())) {
                                        amount = intradayMovementTotal.get(0).add(dailyMovement.getPrice()).multiply(BigDecimal.valueOf(dailyMovement.getAmount()))
                                                .multiply(currenciesDayByDay.get(date).stream().filter(x -> x.getFirst().equals(dailyMovement.getCurrency())).findFirst().orElseThrow().getSecond());

                                    } else {
                                        amount = intradayMovementTotal.get(0).subtract(dailyMovement.getPrice().multiply(BigDecimal.valueOf(dailyMovement.getAmount())));
                                    }
                                    intradayMovementTotal.add(0, amount);
                                    removedMoney.add(0, amount);
                                } else {
                                    throw new RuntimeException("Error while updating portfolioValue: Cannot sell more then what you have in portfolio");
                                }
                            } else {
                                throw new RuntimeException("Error while updating portfolioValue: Cannot sell asset not present in portfolio");
                            }
                        }
                    });

                    //delete the record for the date in analysis
                    repository.deleteByPortfolioIdAndDate(movementBin.getPortfolioId(), date);
                    //calcolare la percentuale di guadagno/perdita (tenere in considerazione che non si può andare sottozero
                    // con i soldi inseriti e che bisogna aggiornare il counter dei soldi ritirati dall'investimento)
                    finalAssetAllocation.forEach(asset -> {
                        BigDecimal assetPrice = assetDayByDay.get(date).stream().filter(e -> e.getFirst().equals(asset.getSymbolId())).findFirst().orElseThrow().getSecond();

                        portfolioValue.add(0, portfolioValue.get(0).add(assetPrice.multiply(BigDecimal.valueOf(asset.getAmount()))));
                    });

                    PortfolioPrivacyInfoEntity portfolioPrivacyInfoEntity = new PortfolioPrivacyInfoEntity();
                    portfolioPrivacyInfoEntity.setPortfolioID(movementBin.getPortfolioId());
                    PortfolioHistoryEntity lastPortfolioRecord = repository.findTopByPortfolioIDOrderByDateDesc(movementBin.getPortfolioId()).orElse(new PortfolioHistoryEntity());
                    PortfolioHistoryEntity portfolioHistory = new PortfolioHistoryEntity();
                    portfolioHistory.setPortfolioID(movementBin.getPortfolioId());
                    portfolioHistory.setDate(movementBin.getDate());
                    portfolioHistory.setAmount(Optional.ofNullable(lastPortfolioRecord.getAmount())
                            .map(e -> {
                                if (lastPortfolioRecord.getAmount().add(Optional.ofNullable(intradayMovementTotal.get(0)).orElse(BigDecimal.ZERO)).compareTo(BigDecimal.ZERO) < 1) {
                                    return BigDecimal.ONE;
                                }
                                return lastPortfolioRecord.getAmount().add(intradayMovementTotal.get(0));
                            })
                            .orElse(intradayMovementTotal.get(0)));
                    portfolioHistory.setExtra_value(Optional.ofNullable(removedMoney.get(0)).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(-1)));
                    portfolioHistory.setCountervail(portfolioValue.get(0));
                    //andamento percentuale
                    portfolioHistory.setPercentageValue(portfolioHistory.getCountervail().subtract(portfolioHistory.getAmount()).divide(portfolioHistory.getAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100);
                    repository.save(portfolioHistory);
                });

            } catch (Exception e) {
                throw new RuntimeException("There was an error while updating old movements, " + e);
            }
        }
    }

    /**
     * Method to insert a new record for each portfolio, this method should be called automatically each day at midnight
     */
    @Override
    public void insertNewDay() {
        Map<String, BigDecimal> prices = new HashMap<>();
        List<Long> portfolioIDs = portfolioInfoRepository.findAllPortfolioIds();

        //prendere tutte le transazioni per vedere quanto di ogni asset si ha
        List<ServiceInstance> instances = discoveryClient.getInstances("transaction");
        if (instances != null && !instances.isEmpty()) {
            // Get a random instance
            String url = instances.get(new Random().nextInt(instances.size())).getUri().toString();
            RestTemplate restTemplate = new RestTemplate();
            Optional.ofNullable(portfolioIDs).ifPresent(x -> x.forEach(portfolioID -> {
                ResponseEntity<GetPortfolioAssetAllocationBin> responsePortfolioComposition = restTemplate.exchange(
                        url + "/get-by-portfolio/" + portfolioID.toString() + "/assets-qty",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<GetPortfolioAssetAllocationBin>() {
                        });
                GetPortfolioAssetAllocationBin assetAllocationBin = responsePortfolioComposition.getBody();
                //fare più o meno degli asset in base alla tabella dei movimenti del giorno
                List<IntradayMovementsEntity> intradayMovements = intradayMovementsRepository.findByPortfolioID(portfolioID);
                //lista per avere la quantità di soldi movimentati nella giornata che poi andranno a modificare il valore della colonna amount
                List<BigDecimal> intradayMovementTotal = new ArrayList<>(List.of(BigDecimal.ZERO));
                List<BigDecimal> removedMoney = new ArrayList<>(List.of(BigDecimal.ZERO));
                List<BigDecimal> portfolioValue = new ArrayList<>(List.of(BigDecimal.ZERO));
                List<GetAssetQuantityBin> assets = Optional.ofNullable(assetAllocationBin).map(GetPortfolioAssetAllocationBin::getAssetAllocation).orElse(new ArrayList<>());
                intradayMovements.forEach(movement -> {
                    //aggiungere o rimuovere l'asset dal totale
                    if (TransactionTypeEnum.BUY.equals(movement.getType())) {
                        //case BUY
                        Optional<GetAssetQuantityBin> asset = assets.stream().filter(e -> movement.getSymbolId().equals(e.getSymbolId())).findFirst();
                        if (asset.isPresent()) {
                            assets.get(assets.indexOf(asset.get())).setAmount(asset.get().getAmount() + movement.getAmount());
                        } else {
                            GetAssetQuantityBin newAsset = GetAssetQuantityBin.builder().symbolId(movement.getSymbolId()).amount(movement.getAmount()).build();
                            assets.add(newAsset);
                        }
                        if (!"EUR".equals(movement.getCurrency())) {
                            //asset price not in EUR, make the conversion before adding to the list
                            if (prices.containsKey(movement.getCurrency())) {
                                intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                        .multiply(prices.get(movement.getCurrency()))));
                            } else {
                                List<ServiceInstance> assetService = discoveryClient.getInstances("asset");
                                if (assetService != null && !assetService.isEmpty()) {
                                    // Get a random instance
                                    String assetUrl = assetService.get(new Random().nextInt(instances.size())).getUri().toString();

                                    Currency currencyConversion = restTemplate.exchange(
                                        assetUrl + "/api/v1/currency/" + movement.getCurrency() + "/EUR",
                                        HttpMethod.GET,
                                        null,
                                        new ParameterizedTypeReference<Currency>() {
                                        }).getBody();
                                    BigDecimal currencyConversionAmount = Optional.ofNullable(currencyConversion).map(Currency::getPriceList).flatMap(el -> el.stream().findFirst()).orElse(BigDecimal.ONE);
                                    prices.put(movement.getCurrency(), currencyConversionAmount);
                                    intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                            .multiply(currencyConversionAmount)));
                                } else {
                                    prices.put(movement.getCurrency(), BigDecimal.ONE);
                                    intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                            .multiply(BigDecimal.ONE)));
                                }
                            }
                        } else {
                            //asset price in EUR
                            intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))));
                        }
                    } else {
                        //case SELL
                        Optional<GetAssetQuantityBin> asset = assets.stream().filter(e -> movement.getSymbolId().equals(e.getSymbolId())).findFirst();
                        if (asset.isPresent()) {
                            if ((asset.get().getAmount() - movement.getAmount()) >= 0) {
                                assets.get(assets.indexOf(asset.get())).setAmount(asset.get().getAmount() - movement.getAmount());
                                if (!"EUR".equals(movement.getCurrency())) {
                                    if (prices.containsKey(movement.getCurrency())) {
                                        BigDecimal amount = intradayMovementTotal.get(0).subtract(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                                .multiply(prices.get(movement.getCurrency())));
                                        intradayMovementTotal.add(0, amount);
                                        removedMoney.add(0, amount);
                                    } else {
                                        List<ServiceInstance> assetService = discoveryClient.getInstances("asset");
                                        if (assetService != null && !assetService.isEmpty()) {
                                            // Get a random instance
                                            String assetUrl = assetService.get(new Random().nextInt(instances.size())).getUri().toString();

                                            Currency currencyConversion = restTemplate.exchange(
                                                    assetUrl + "/api/v1/currency/" + movement.getCurrency() + "/EUR",
                                                    HttpMethod.GET,
                                                    null,
                                                    new ParameterizedTypeReference<Currency>() {
                                                    }).getBody();
                                            BigDecimal currencyConversionAmount = Optional.ofNullable(currencyConversion).map(Currency::getPriceList).flatMap(el -> el.stream().findFirst()).orElse(BigDecimal.ONE);
                                            prices.put(movement.getCurrency(), currencyConversionAmount);
                                            BigDecimal amount = intradayMovementTotal.get(0).subtract(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                                    .multiply(currencyConversionAmount));
                                            intradayMovementTotal.add(0, amount);
                                            removedMoney.add(0, amount);
                                        } else {
                                            prices.put(movement.getCurrency(), BigDecimal.ONE);
                                            BigDecimal amount = intradayMovementTotal.get(0).subtract(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                                    .multiply(BigDecimal.ONE));
                                            intradayMovementTotal.add(0, amount);
                                            removedMoney.add(0, amount);
                                        }

                                    }
                                } else {
                                    BigDecimal amount = intradayMovementTotal.get(0).subtract(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount())));
                                    intradayMovementTotal.add(0, amount);
                                    removedMoney.add(0, amount);
                                }
                            } else {
                                throw new RuntimeException("Error while updating portfolioValue: Cannot sell more then what you have in portfolio");
                            }
                        } else {
                            throw new RuntimeException("Error while updating portfolioValue: Cannot sell asset not present in portfolio");
                        }
                    }
                });

                //calcolare la percentuale di guadagno/perdita (tenere in considerazione che non si può andare sottozero
                // con i soldi inseriti e che bisogna aggiornare il counter dei soldi ritirati dall'investimento)
                assets.forEach(asset -> {
                    BigDecimal assetPrice;
                    if (prices.containsKey(asset.getSymbolId())) {
                        assetPrice = prices.get(asset.getSymbolId());
                    } else {
                        //calcolare il valore totale degli asset (chiamata api al servizio asset, inserire il prezzo dell'asset in una mappa così da non rifare le stesse chiamate)
                        List<ServiceInstance> assetService = discoveryClient.getInstances("asset");
                        if (assetService != null && !assetService.isEmpty()) {
                            // Get a random instance
                            String assetUrl = assetService.get(new Random().nextInt(instances.size())).getUri().toString();

                            Asset assetInfo = restTemplate.exchange(
                                    assetUrl + "/api/v1/asset/" + asset.getSymbolId(),
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<Asset>() {
                                    }).getBody();
                            assetPrice = Optional.ofNullable(assetInfo).map(Asset::getPrices).flatMap(e -> e.stream().findFirst()).orElse(BigDecimal.ZERO);
                            prices.put(asset.getSymbolId(), assetPrice);
                        } else {
                            throw new RuntimeException("Service not found");
                        }
                    }

                    portfolioValue.add(0, portfolioValue.get(0).add(assetPrice.multiply(BigDecimal.valueOf(asset.getAmount()))));
                });

                PortfolioPrivacyInfoEntity portfolioPrivacyInfoEntity = new PortfolioPrivacyInfoEntity();
                portfolioPrivacyInfoEntity.setPortfolioID(portfolioID);
                PortfolioHistoryEntity lastPortfolioRecord = repository.findTopByPortfolioIDOrderByDateDesc(portfolioID).orElse(new PortfolioHistoryEntity());
                PortfolioHistoryEntity portfolioHistory = new PortfolioHistoryEntity();
                portfolioHistory.setPortfolioID(portfolioID);
                portfolioHistory.setDate(LocalDate.now());
                portfolioHistory.setAmount(Optional.ofNullable(lastPortfolioRecord.getAmount())
                        .map(e -> {
                            if (lastPortfolioRecord.getAmount().add(Optional.ofNullable(intradayMovementTotal.get(0)).orElse(BigDecimal.ZERO)).compareTo(BigDecimal.ZERO) < 1) {
                                return BigDecimal.ONE;
                            }
                            return lastPortfolioRecord.getAmount().add(intradayMovementTotal.get(0));
                        })
                        .orElse(intradayMovementTotal.get(0)));
                portfolioHistory.setExtra_value(Optional.ofNullable(removedMoney.get(0)).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(-1)));
                portfolioHistory.setCountervail(portfolioValue.get(0));
                //andamento percentuale
                portfolioHistory.setPercentageValue(portfolioHistory.getCountervail().subtract(portfolioHistory.getAmount()).divide(portfolioHistory.getAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100);
                repository.save(portfolioHistory);
            }));
        }

        intradayMovementsRepository.deleteAll();
    }

    @Override
    public void insertNewPortfolio(Long id) {
        PortfolioPrivacyInfoEntity portfolioPrivacyInfoEntity = new PortfolioPrivacyInfoEntity();
        portfolioPrivacyInfoEntity.setPortfolioID(id);
        portfolioPrivacyInfoEntity.setSharable(true);
        portfolioInfoRepository.save(portfolioPrivacyInfoEntity);
    }

    /**
     * Function used to remove data related to a portfolio in each table
     */
    @Override
    public void deletePortfolio(Long id) {
        repository.deleteByPortfolioID(id);
        intradayMovementsRepository.deleteByPortfolioID(id);
        portfolioInfoRepository.deleteByPortfolioID(id);
    }

    /**
     * Methods that returns data for a specific portfolio
     */
    @Override
    public List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin) {
        List<PortfolioHistoryEntity> records = repository.findByPortfolioIDAndDateAfter(getPortfolioHistoryBin.getPortfolioId(),
                DurationIntervalEnum.getDateFromNow(getPortfolioHistoryBin.getDurationIntervalEnum()));
        return records.stream().map(this::fromEntityToObject).toList();
    }

    /**
     * Function to get the top 10 portfolios based on percentageValue
     */
    @Override
    public List<PortfolioHistory> getRanking() {
        List<PortfolioHistoryEntity> records = repository.findTop10ByPercentageValue();
        return records.stream().map(this::fromEntityToObject).toList();
    }

    /**
     * Function to update the privacy settings of a specific portfolio, this is used for ranking porpoises
     */
    @Override
    public void updatePrivacySetting(UpdatePortfolioInfoBin infoBin) {
        portfolioInfoRepository.updateSharabilityById(infoBin.getId(), infoBin.isSharable());
    }

    private PortfolioHistory fromEntityToObject(PortfolioHistoryEntity entity) {
        return PortfolioHistory.builder()
                .portfolioID(entity.getPortfolioID())
                .id(entity.getId())
                .date(entity.getDate())
                .amount(entity.getAmount())
                .countervail(entity.getCountervail())
                .percentageValue(entity.getPercentageValue())
                .extraValue(entity.getExtra_value())
                .build();
    }


}
