package services.impl;

import models.Currency;
import models.*;
import models.entities.IntradayMovementsEntity;
import models.entities.PortfolioHistoryEntity;
import models.entities.PortfolioPrivacyInfoEntity;
import models.enums.DurationIntervalEnum;
import models.enums.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
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

    /*
    TODO
     
    bisogna ancora fare la funzione che permette di aggiornare lo storico passato di un portfolio, una volta passata una data
    di partenza per ricalcolare il tutto
    (input -> lista di transazioni ordinate per data)
    (funzionamento -> partire dalla data di partenza e aggiornare tutti i record andando a variare il controvalore del portfolio)


     */

    /**
     * Method to update a portfolio, this method is called each time there is a new transaction in the portfolio history, the input will be from RabbitMQ
     */
    @Override
    public void insertIntradayMovement(IntradayMovementBin intradayMovementBin) {
        IntradayMovementsEntity entity = new IntradayMovementsEntity();
        entity.setPortfolioId(intradayMovementBin.getPortfolioId());
        entity.setCurrency(intradayMovementBin.getCurrency());
        entity.setPrice(intradayMovementBin.getPrice());
        entity.setDate(intradayMovementBin.getDate());
        entity.setAmount(intradayMovementBin.getAmount());
        entity.setType(intradayMovementBin.getType());
        entity.setSymbolId(intradayMovementBin.getSymbolId());
        intradayMovementsRepository.save(entity);
    }

    /**
     * Method to insert a new record for each portfolio, this method is called automatically each day at midnight
     */
    @Override
    public void insertNewDay() {
        Map<String, BigDecimal> prices = new HashMap<>();
        List<Long> portfolioIDs = portfolioInfoRepository.findAllPortfolioIds();
        //prendere tutte le transazioni per vedere quanto di ogni asset si ha
        Optional.ofNullable(portfolioIDs).ifPresent(x -> x.forEach(portfolioID -> {
            ResponseEntity<GetPortfolioAssetAllocationBin> responsePortfolioComposition = restTemplate.getForEntity("/get-by-portfolio/" + portfolioID.toString() + "/assets-qty", GetPortfolioAssetAllocationBin.class);
            GetPortfolioAssetAllocationBin assetAllocationBin = responsePortfolioComposition.getBody();
            //fare più o meno degli asset in base alla tabella dei movimenti del giorno
            List<IntradayMovementsEntity> intradayMovements = intradayMovementsRepository.findByPortfolioID(portfolioID);
            //lista per avere la quantità di soldi movimentati nella giornata che poi andranno a modificare il valore della colonna amount
            List<BigDecimal> intradayMovementTotal = new ArrayList<>(List.of(BigDecimal.ZERO));
            List<BigDecimal> removedMoney = new ArrayList<>(List.of(BigDecimal.ZERO));
            List<BigDecimal> portfolioValue = new ArrayList<>(List.of(BigDecimal.ZERO));
            intradayMovements.forEach(movement -> {
                //aggiungere o rimuovere l'asset dal totale
                List<GetAssetQuantityBin> assets = Optional.ofNullable(assetAllocationBin).map(GetPortfolioAssetAllocationBin::getAssetAllocation).orElse(new ArrayList<>());
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
                        //asset price not in EUR, make the coversion before adding to the list
                        if (prices.containsKey(movement.getCurrency())) {
                            intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                    .multiply(prices.get(movement.getCurrency()))));
                        } else {
                            Currency currencyConversion = restTemplate.getForEntity("/api/v1/currency/" + movement.getCurrency() + "/EUR", Currency.class).getBody();
                            BigDecimal currencyConversionAmount = Optional.ofNullable(currencyConversion).map(Currency::getPriceList).flatMap(el -> el.stream().findFirst()).orElse(BigDecimal.ONE);
                            prices.put(movement.getCurrency(), currencyConversionAmount);
                            intradayMovementTotal.add(0, intradayMovementTotal.get(0).add(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                    .multiply(currencyConversionAmount)));
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
                                    Currency currencyConversion = restTemplate.getForEntity("/api/v1/currency/" + movement.getCurrency() + "/EUR", Currency.class).getBody();
                                    BigDecimal currencyConversionAmount = Optional.ofNullable(currencyConversion).map(Currency::getPriceList).flatMap(el -> el.stream().findFirst()).orElse(BigDecimal.ONE);
                                    prices.put(movement.getCurrency(), currencyConversionAmount);
                                    BigDecimal amount = intradayMovementTotal.get(0).subtract(movement.getPrice().multiply(BigDecimal.valueOf(movement.getAmount()))
                                            .multiply(currencyConversionAmount));
                                    intradayMovementTotal.add(0, amount);
                                    removedMoney.add(0, amount);
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


                assets.forEach(asset -> {
                    BigDecimal assetPrice;
                    if (prices.containsKey(asset.getSymbolId())) {
                        assetPrice = prices.get(asset.getSymbolId());
                    } else {
                        //calcolare il valore totale degli asset (chiamata api al servizio asset, inserire il prezzo dell'asset in una mappa così da non rifare le stesse chiamate)
                        Asset assetInfo = restTemplate.getForEntity("/api/v1/asset/" + asset.getSymbolId(), Asset.class).getBody();
                        assetPrice = Optional.ofNullable(assetInfo).map(Asset::getPrices).flatMap(e -> e.stream().findFirst()).orElse(BigDecimal.ZERO);
                        prices.put(asset.getSymbolId(), assetPrice);
                    }

                    portfolioValue.add(0, portfolioValue.get(0).add(assetPrice.multiply(BigDecimal.valueOf(asset.getAmount()))));
                });
            });

            //calcolare la percentuale di guadagno/perdita (tenere in considerazione che non si può andare sottozero
            // con i soldi inseriti e che bisogna aggiornare il counter dei soldi ritirati dall'investimento)

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
