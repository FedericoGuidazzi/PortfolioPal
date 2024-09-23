package com.example.portfolio_history.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.portfolio_history.models.Asset;
import com.example.portfolio_history.models.Currency;
import com.example.portfolio_history.models.PortfolioHistory;
import com.example.portfolio_history.models.bin.GetAssetQuantityBin;
import com.example.portfolio_history.models.bin.GetPortfolioHistoryBin;
import com.example.portfolio_history.models.bin.MovementBin;
import com.example.portfolio_history.models.bin.RabbitTransactionBin;
import com.example.portfolio_history.models.entities.PortfolioHistoryEntity;
import com.example.portfolio_history.models.enums.DurationIntervalEnum;
import com.example.portfolio_history.models.enums.TransactionTypeEnum;
import com.example.portfolio_history.repositories.PortfolioHistoryRepository;
import com.example.portfolio_history.repositories.PortfolioRepository;
import com.example.portfolio_history.services.PortfolioHistoryService;

@Service
public class PortfolioHistoryServiceImpl implements PortfolioHistoryService {

	@Autowired
	private PortfolioHistoryRepository repository;

	@Autowired
	private PortfolioRepository portfolioInfoRepository;

	@Autowired
	private DiscoveryClient discoveryClient;

	/**
	 * Method to update old movement, this means that starting from the initial
	 * modifying date all the portfolio records will be modified
	 *
	 * @param movementBin
	 */
	@Override
	public void updateOldMovements(RabbitTransactionBin movementBin) {

		try {
			this.update(Map.of(
					movementBin.getPortfolioId(), Pair.of(movementBin.getDate(), true)));

		} catch (Exception e) {
			throw new RuntimeException("There was an error while updating old movements: " + e);
		}

	}


	/**
	 * Method to insert a new record for each portfolio, this method should be
	 * called automatically each day at midnight
	 */
	@Override
	public void insertNewDay() {
		List<Long> portfolioIDs = Optional.ofNullable(portfolioInfoRepository.findAllPortfolioIds()).orElse(List.of());

		try {
			portfolioIDs.forEach(portfolioID -> {
				this.update(Map.of(
						portfolioID,
						Pair.of(LocalDate.now().minusDays(1), false)));
			});
		} catch (Exception e) {
			throw new RuntimeException("There was an error while inserting new day, " + e.getMessage());
		}
	}

	/**
	 * Methods that returns data for a specific portfolio
	 */
	@Override
	public List<PortfolioHistory> getPortfolioHistory(GetPortfolioHistoryBin getPortfolioHistoryBin) {
		List<PortfolioHistoryEntity> records;
		if (getPortfolioHistoryBin.getDurationIntervalEnum() == null ||
				getPortfolioHistoryBin.getDurationIntervalEnum().equals(DurationIntervalEnum.MAX)) {
			records = repository
					.findByPortfolioId(getPortfolioHistoryBin.getPortfolioId());
		} else {
			records = repository.findByPortfolioIdAndDateAfter(
					getPortfolioHistoryBin.getPortfolioId(),
					DurationIntervalEnum.getDateFromNow(getPortfolioHistoryBin.getDurationIntervalEnum()));
		}
		return records.stream().map(this::fromEntityToObject).toList();
	}

	/**
	 * Converts a PortfolioHistoryEntity object to a PortfolioHistory object.
	 *
	 * @param entity the PortfolioHistoryEntity to be converted
	 * @return a PortfolioHistory object containing the data from the entity
	 */
	private PortfolioHistory fromEntityToObject(PortfolioHistoryEntity entity) {
		return PortfolioHistory.builder()
				.portfolioId(entity.getPortfolioId())
				.id(entity.getId())
				.date(entity.getDate())
				.investedAmount(entity.getInvestedAmount())
				.countervail(entity.getCountervail())
				.percentageValue(entity.getPercentageValue())
				.withdrawnAmount(entity.getWithdrawnAmount())
				.build();
	}

	/**
	 * Retrieves a random instance URL for a given service ID.
	 *
	 * @param serviceId the ID of the service for which to retrieve an instance URL
	 * @return a randomly selected instance URL as a String
	 * @throws RuntimeException if no instances of the service are found
	 */
	private String getRandomInstanceUrl(String serviceId) {
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
		if (instances == null || instances.isEmpty()) {
			throw new RuntimeException("Service " + serviceId + " not found");
		}

		return instances.get(new Random().nextInt(instances.size())).getUri().toString();
	}

	/**
	 * Updates the portfolio history records for the given portfolios.
	 *
	 * @param portfoliosMap a map containing the portfolio IDs as keys and pairs of
	 *                      the initial date and a boolean indicating whether the
	 *                      operation is an update operation as values
	 */
	private void update(Map<Long, Pair<LocalDate, Boolean>> portfoliosMap) {
		portfoliosMap.forEach((id, pair) -> {
			LocalDate startDate = pair.getFirst();
			boolean isUpdateOperation = Optional.ofNullable(pair.getSecond())
					.orElse(false);

			List<LocalDate> dates = startDate.datesUntil(LocalDate.now()).toList();

			RestTemplate restTemplate = new RestTemplate();
			// STEP1 get all the transactions of a portfolio starting from the initial date
			String transactionUrl = this.getRandomInstanceUrl("transaction");
			// Request all the transactions of a portfolio starting from the initial date
			ResponseEntity<List<MovementBin>> responseEntity = restTemplate.exchange(
					transactionUrl + "/api/v1/transaction/get-by-portfolio/"
							+ id
							+ "?date=" + startDate.toString(),
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<MovementBin>>() {
					});
			List<MovementBin> movements = Optional.ofNullable(responseEntity.getBody()).orElse(List.of());

			// Request all the assets of a portfolio starting from the initial date
			ResponseEntity<List<GetAssetQuantityBin>> assetResponseEntityList = restTemplate.exchange(
					transactionUrl + "/api/v1/transaction/get-by-portfolio/"
							+ id
							+ "/assets-qty?date=" + startDate.toString(),
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<GetAssetQuantityBin>>() {
					});
			List<GetAssetQuantityBin> assetResponseList = Optional.ofNullable(assetResponseEntityList.getBody())
					.orElse(List.of());
			Map<String, Double> assetAllocation = assetResponseList.isEmpty()
					? new HashMap<>()
					: assetResponseList.stream()
							.collect(
									Collectors.toMap(GetAssetQuantityBin::getSymbolId, GetAssetQuantityBin::getAmount));

			// This map will contain all the information regarding asset price in the
			// selected period
			Map<String, Asset> assetMap = new HashMap<>();
			// This map will contain all the information regarding currency price in the
			// selected period
			Map<String, Currency> currenciesMap = new HashMap<>();

			// This set will contain all the asset in a portfolio and in the transactions
			Set<String> assetsIdsInPortfolio = movements.stream()
					.map(MovementBin::getSymbolId)
					.collect(Collectors.toSet());
			assetsIdsInPortfolio.addAll(assetAllocation.keySet());

			String assetUrl = this.getRandomInstanceUrl("asset");
			assetsIdsInPortfolio.forEach(asset -> {
				ResponseEntity<Asset> assetResponseEntity = restTemplate.exchange(
						assetUrl + "/api/v1/asset/" + asset + "?startDate="
								+ startDate.toString(),
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<Asset>() {
						});

				assetMap.put(asset, assetResponseEntity.getBody());
			});

			assetMap.values()
					.stream()
					.map(Asset::getCurrency)
					.distinct()
					.filter(currencyName -> !"EUR".equals(currencyName))
					.forEach(currencyName -> {
						ResponseEntity<Currency> currencyResponseEntity = restTemplate.exchange(
								assetUrl + "/api/v1/currency/" + currencyName + "/EUR?startDate="
										+ startDate.toString(),
								HttpMethod.GET,
								null,
								new ParameterizedTypeReference<Currency>() {
								});

						currenciesMap.put(currencyName, currencyResponseEntity.getBody());
					});

			// create a map to store all the infos required to calculate the portfolio value
			// (date, [(assetId, price)])
			Map<LocalDate, List<Pair<String, BigDecimal>>> assetDayByDay = new HashMap<>();
			// (date, [(currencyId, price)])
			Map<LocalDate, List<Pair<String, BigDecimal>>> currenciesDayByDay = new HashMap<>();

			dates.forEach(date -> {
				List<Pair<String, BigDecimal>> assetList = new ArrayList<>();
				List<Pair<String, BigDecimal>> currenciesList = new ArrayList<>();
				// Insert asset and currency price for the days that doesn't have it
				assetMap.forEach((assetName, asset) -> {
					// If the asset is present in assetMap, take the price of the day otherwise take
					// the price of the day before
					int index = asset.getDates().indexOf(date);
					BigDecimal price = BigDecimal.ONE;
					if (index != -1) {
						price = asset.getPrices().get(index);
					} else {
						if (assetDayByDay.containsKey(date.minusDays(1))) {
							List<Pair<String, BigDecimal>> pairList = assetDayByDay
									.get(date.minusDays(1));
							price = pairList.stream()
									.filter(e -> e.getFirst().equals(assetName))
									.map(Pair::getSecond)
									.findFirst()
									.orElse(BigDecimal.ONE);
						}
					}
					assetList.add(Pair.of(assetName, price));
				});
				assetDayByDay.put(date, assetList);

				currenciesMap.forEach((currencyName, currency) -> {
					// If the currency is present in currencyMap, take the price of the day
					// otherwise take the price of the day before
					int index = currency.getDateList().indexOf(date);
					BigDecimal price = BigDecimal.ONE;
					if (index != -1) {
						price = currency.getPriceList().get(index);
					} else {
						if (currenciesDayByDay.containsKey(date.minusDays(1))) {
							List<Pair<String, BigDecimal>> pairList = currenciesDayByDay
									.get(date.minusDays(1));
							price = pairList.stream()
									.filter(e -> e.getFirst().equals(currencyName))
									.map(Pair::getSecond)
									.findFirst()
									.orElse(BigDecimal.ONE);
						}
					}
					currenciesList.add(Pair.of(currencyName, price));
				});
				currenciesDayByDay.put(date, currenciesList);

				// Amount of money invested in the portfolio daily
				BigDecimal dailyInvestedAmount = BigDecimal.ZERO;
				// Amount of money removed from the portfolio daily
				BigDecimal dailyWithdrawnAmount = BigDecimal.ZERO;
				// Portfolio value daily
				BigDecimal dailyPortfolioValue = BigDecimal.ZERO;

				// Get all the transactions for the date in analysis
				List<MovementBin> dailyMovements = movements.stream()
						.filter(e -> date.equals(e.getDate()))
						.toList();

				for (MovementBin dailyMovement : dailyMovements) {
					// Update the asset allocation based on the transaction of the day in analysis

					int multiplyCoef = TransactionTypeEnum.BUY
							.equals(TransactionTypeEnum.fromValue(dailyMovement.getType())) ? 1 : -1;

					assetAllocation.put(
							dailyMovement.getSymbolId(),
							assetAllocation.getOrDefault(dailyMovement.getSymbolId(), 0.0)
									+ multiplyCoef * dailyMovement.getAmount());

					// Update the amount of money invested in the portfolio
					BigDecimal amount = dailyMovement.getPrice()
							.multiply(BigDecimal.valueOf(dailyMovement.getAmount()));
					// asset price not in EUR make the coversion
					if (!"EUR".equals(dailyMovement.getCurrency())) {
						amount = amount.multiply(currenciesDayByDay
								.get(date)
								.stream()
								.filter(x -> x.getFirst()
										.equals(dailyMovement
												.getCurrency()))
								.findFirst()
								.orElseThrow()
								.getSecond());
					}
					if (multiplyCoef < 0) {
						dailyWithdrawnAmount = dailyWithdrawnAmount.add(amount);
					} else {
						dailyInvestedAmount = dailyInvestedAmount.add(amount);
					}
				}

				if (isUpdateOperation) {
					// delete the record for the date in analysis
					repository.findByPortfolioIdAndDate(id, date).ifPresent(e -> {
						repository.delete(e);
					});
				}

				// Update the portfolio value
				for (String assetId : assetAllocation.keySet()) {
					BigDecimal assetPrice = assetDayByDay.get(date).stream()
							.filter(e -> e.getFirst().equals(assetId))
							.findFirst().orElseThrow()
							.getSecond();
					dailyPortfolioValue = dailyPortfolioValue.add(assetPrice.multiply(
							BigDecimal.valueOf(assetAllocation.get(assetId))));
				}

				// Update the amount of money invested, the amount of money removed and the
				// value of the portfolio in the repository
				PortfolioHistoryEntity lastPortfolioRecord = repository
						.findByPortfolioIdAndDate(id, date.minusDays(1))
						.orElse(new PortfolioHistoryEntity());
				PortfolioHistoryEntity newHistoryRecord = PortfolioHistoryEntity
						.builder()
						.portfolioId(id)
						.date(date)
						.investedAmount(Optional.ofNullable(lastPortfolioRecord.getInvestedAmount())
								.orElse(BigDecimal.ZERO)
								.add(dailyInvestedAmount))
						.withdrawnAmount(Optional.ofNullable(lastPortfolioRecord.getWithdrawnAmount())
								.orElse(BigDecimal.ZERO)
								.add(dailyWithdrawnAmount))
						.countervail(dailyPortfolioValue)
						.build();

				// Set the percentage value of the portfolio
				if (newHistoryRecord.getCountervail().compareTo(BigDecimal.ZERO) == 0) {
					newHistoryRecord.setPercentageValue(0.0);
				} else {
					BigDecimal difference = newHistoryRecord.getCountervail()
							.add(newHistoryRecord.getWithdrawnAmount())
							.subtract(newHistoryRecord.getInvestedAmount());
					BigDecimal ratio = difference.divide(newHistoryRecord.getInvestedAmount(), 4, RoundingMode.HALF_UP);
					newHistoryRecord.setPercentageValue(ratio.doubleValue() * 100);
				}

				repository.save(newHistoryRecord);
			});
		});

	}

}
