package com.example.transaction.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;
import com.example.transaction.models.entities.TransactionEntity;
import com.example.transaction.models.enums.TransactionType;
import com.example.transaction.repositories.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public Transaction insertTransaction(PostTransactionBin transactionBin) throws CustomException {

		TransactionEntity newTransaction = TransactionEntity.builder()
				.type(Optional.ofNullable(
						TransactionType.fromValue(transactionBin.getType()))
						.orElseThrow(() -> new CustomException("Invalid transaction type")))
				.date(transactionBin.getDate())
				.amount(transactionBin.getAmount())
				.price(transactionBin.getPrice())
				.symbolId(transactionBin.getSymbolId())
				.portfolioId(transactionBin.getPortfolioId())
				.currency(transactionBin.getCurrency())
				.build();

		this.checkAssetQty(List.of(newTransaction));

		TransactionEntity entity = transactionRepository.save(newTransaction);

		return Transaction.builder()
				.id(entity.getId())
				.type(transactionBin.getType())
				.date(entity.getDate())
				.amount(entity.getAmount())
				.price(entity.getPrice())
				.symbolId(entity.getSymbolId())
				.currency(entity.getCurrency())
				.build();
	}

	@Override
	public List<Transaction> getAllTransactionsByPortfolioId(long portfolioId) {
		List<TransactionEntity> entities = transactionRepository.findAllByPortfolioId(portfolioId);

		return entities.stream()
				.map(entity -> Transaction.builder()
						.id(entity.getId())
						.type(entity.getType().getPersistedValue())
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.currency(entity.getCurrency())
						.portfolioId(entity.getPortfolioId())
						.build())
				.toList();
	}

	@Override
	public List<Transaction> getTransactionsByPortfolioIdAndSymbolId(long portfolioId, String symbolId) {
		List<TransactionEntity> entities = transactionRepository.findAllByPortfolioIdAndSymbolId(portfolioId, symbolId);

		return entities.stream()
				.map(entity -> Transaction.builder()
						.id(entity.getId())
						.type(entity.getType().getPersistedValue())
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.currency(entity.getCurrency())
						.portfolioId(entity.getPortfolioId())
						.build())
				.toList();
	}

	@Override
	public Transaction getTransactionById(long id) throws CustomException {
		TransactionEntity entity = transactionRepository.findById(id)
				.orElseThrow(() -> new CustomException("Transaction not found"));

		return Transaction.builder()
				.id(entity.getId())
				.type(entity.getType().getPersistedValue())
				.date(entity.getDate())
				.amount(entity.getAmount())
				.price(entity.getPrice())
				.symbolId(entity.getSymbolId())
				.currency(entity.getCurrency())
				.portfolioId(entity.getPortfolioId())
				.build();
	}

	@Override
	public Transaction updateTransaction(PutTransactionBin transactionBin) throws CustomException {

		if (!transactionRepository.existsById(transactionBin.getId())) {
			throw new CustomException("Transaction not found");
		}

		TransactionEntity entity = TransactionEntity.builder()
				.id(transactionBin.getId())
				.type(Optional.ofNullable(
						TransactionType.fromValue(transactionBin.getTransaction().getType()))
						.orElseThrow(() -> new CustomException("Invalid transaction type")))
				.date(transactionBin.getTransaction().getDate())
				.amount(transactionBin.getTransaction().getAmount())
				.price(transactionBin.getTransaction().getPrice())
				.symbolId(transactionBin.getTransaction().getSymbolId())
				.portfolioId(transactionBin.getTransaction().getPortfolioId())
				.currency(transactionBin.getTransaction().getCurrency())
				.build();

		this.checkAssetQty(List.of(entity));

		entity = transactionRepository.save(entity);

		return Transaction.builder()
				.id(entity.getId())
				.type(transactionBin.getTransaction().getType())
				.date(entity.getDate())
				.amount(entity.getAmount())
				.price(entity.getPrice())
				.symbolId(entity.getSymbolId())
				.currency(entity.getCurrency())
				.portfolioId(entity.getPortfolioId())
				.build();
	}

	@Override
	public Transaction deleteTransaction(long id) {
		Transaction deletedTransaction;
		try {
			deletedTransaction = this.getTransactionById(id);
		} catch (CustomException e) {
			return null;
		}

		transactionRepository.delete(TransactionEntity.builder().id(id).build());
		return deletedTransaction;
	}

	@Override
	public List<Transaction> saveTransactionsFromCsv(UploadBin bin) throws CustomException, IOException {

		List<Transaction> transactions = CsvPortfolioReader.readCsvFile(bin.getInputStream())
				.stream()
				.map(e -> {
					return Transaction.builder()
							.date(LocalDate.parse(e.getDate()))
							.type(Optional.ofNullable(TransactionType.fromValue(e.getType()).getPersistedValue())
									.orElseThrow(() -> new CustomException("Invalid transaction type")))
							.amount(e.getAmount())
							.symbolId(e.getSymbolId())
							.price(BigDecimal.valueOf(e.getPrice()))
							.currency(e.getCurrency())
							.portfolioId(bin.getPortfolioId())
							.build();
				}).toList();
		;

		this.checkAssetQty(transactions.stream()
				.map(entity -> TransactionEntity.builder()
						.id(entity.getId())
						.type(TransactionType.fromValue(entity.getType()))
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.portfolioId(bin.getPortfolioId())
						.currency(entity.getCurrency())
						.build())
				.toList());

		transactionRepository.saveAll(transactions.stream()
				.map(entity -> TransactionEntity.builder()
						.id(entity.getId())
						.type(TransactionType.fromValue(entity.getType()))
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.portfolioId(bin.getPortfolioId())
						.currency(entity.getCurrency())
						.build())
				.toList());

		return transactions;
	}

	@Override
	public List<Transaction> getTransactionsByPortfolioIdAfterDate(GetTransactionByDateBin bin) {
		List<TransactionEntity> list = transactionRepository.findAllByPortfolioIdAndAfterDate(bin.getPortfolioId(),
				bin.getDate());

		return list.stream()
				.map(entity -> Transaction.builder()
						.id(entity.getId())
						.type(entity.getType().getPersistedValue())
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.currency(entity.getCurrency())
						.portfolioId(entity.getPortfolioId())
						.build())
				.toList();
	}

	@Override
	public List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioId(long portfolioId) {
		return this.transactionRepository.findAssetsQtyByPortfolioIdAndDate(portfolioId, LocalDate.now()).stream()
				.filter(
						asset -> asset.getAmount() != 0)
				.collect(Collectors.toList());
	}

	@Override
	public List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioIdAndDate(GetTransactionByDateBin bin) {
		return this.transactionRepository.findAssetsQtyByPortfolioIdAndDate(bin.getPortfolioId(), bin.getDate())
				.stream()
				.filter(
						asset -> asset.getAmount() != 0)
				.collect(Collectors.toList());
	}

	/**
	 * Finds the minimum date from a list of TransactionEntity objects.
	 *
	 * @param transactions the list of TransactionEntity objects to search through
	 * @return an Optional containing the minimum LocalDate if present, otherwise an
	 *         empty Optional
	 */
	private static Optional<LocalDate> findMinDate(List<TransactionEntity> transactions) {
		return transactions.stream()
				.map(TransactionEntity::getDate)
				.min(LocalDate::compareTo);
	}

	/**
	 * Checks the quantity of assets for a given list of new transactions.
	 * 
	 * This method verifies if there are enough assets to perform the transactions
	 * by calculating the asset quantities from the minimum transaction date to the
	 * current date.
	 * If a sell transaction is found and there are not enough assets to sell, a
	 * CustomException is thrown.
	 * 
	 * @param newTransactions the list of new transactions to be checked
	 * @throws CustomException if there are not enough assets to sell for any
	 *                         transaction
	 */
	private void checkAssetQty(List<TransactionEntity> newTransactions) throws CustomException {
		Optional<LocalDate> minDate = findMinDate(newTransactions);

		if (minDate.isEmpty()) {
			return;
		}

		Map<String, Double> assetsQty = this
				.getAssetsQtyByPortfolioIdAndDate(GetTransactionByDateBin.builder()
						.portfolioId(newTransactions.get(0).getPortfolioId())
						.date(minDate.get())
						.build())
				.stream()
				.collect(Collectors.toMap(GetAssetQtyOutputBin::getSymbolId,
						GetAssetQtyOutputBin::getAmount));

		List<TransactionEntity> transactions = this.transactionRepository
				.findAllByPortfolioIdAndAfterDate(newTransactions.get(0).getPortfolioId(),
						minDate.get());
		transactions.addAll(newTransactions);

		for (var date : minDate.get().datesUntil(LocalDate.now()).toList()) {
			Map<String, Double> dailyAssetAllocationMap = transactions.stream()
					.filter(t -> t.getDate().equals(date))
					.collect(Collectors.groupingBy(TransactionEntity::getSymbolId,
							Collectors.summingDouble(t -> {
								return t.getType() == TransactionType.SELL ? -t.getAmount() : t.getAmount();
							})));

			dailyAssetAllocationMap.forEach((asset, amount) -> {
				if (assetsQty.getOrDefault(asset, 0.0) + amount < 0) {
					throw new CustomException("Not enough assets to sell, asset: " + asset);
				}
				assetsQty.put(asset, assetsQty.getOrDefault(asset, 0.0) + amount);
			});

		}

	}

}
