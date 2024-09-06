package com.example.transaction.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.CsvPortfolioReader;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
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
				.build();
	}

	@Override
	public Transaction updateTransaction(PutTransactionBin transactionBin) throws CustomException {

		if (!transactionRepository.existsById(transactionBin.getId())) {
			throw new CustomException("Transaction not found");
		}

		TransactionEntity entity = transactionRepository.save(TransactionEntity.builder()
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
				.build());

		return Transaction.builder()
				.id(entity.getId())
				.type(transactionBin.getTransaction().getType())
				.date(entity.getDate())
				.amount(entity.getAmount())
				.price(entity.getPrice())
				.symbolId(entity.getSymbolId())
				.currency(entity.getCurrency())
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

		List<Transaction> transactions = CsvPortfolioReader.readCsvFile(bin.getInputStream());
		Map<String, Double> assetsQty = this.getAssetsQtyByPortfolioId(bin.getPortfolioId()).stream().collect(
				Collectors.toMap(GetAssetQtyOutputBin::getSymbolId, GetAssetQtyOutputBin::getAmount));
		for (Transaction transaction : transactions) {
			if (TransactionType.fromValue(transaction.getType()) == TransactionType.SELL) {
				if (assetsQty.getOrDefault(transaction.getSymbolId(), 0.0) < transaction.getAmount()) {
					throw new CustomException("Not enough assets to sell");
				}
				assetsQty.put(transaction.getSymbolId(),
						assetsQty.get(transaction.getSymbolId()) - transaction.getAmount());
			} else {
				assetsQty.put(transaction.getSymbolId(),
						assetsQty.getOrDefault(transaction.getSymbolId(), 0.0) + transaction.getAmount());
			}
		}
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
		List<TransactionEntity> list = transactionRepository.findAllByPortfolioIdAndDateAfter(bin.getPortfolioId(),
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
						.build())
				.toList();
	}

	@Override
	public List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioId(long portfolioId) {
		return this.transactionRepository.findAssetsQtyByPortfolioIdAndDate(portfolioId, LocalDate.now());
	}

	@Override
	public List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioIdAndDate(GetTransactionByDateBin bin) {
		return this.transactionRepository.findAssetsQtyByPortfolioIdAndDate(bin.getPortfolioId(), bin.getDate());
	}

}
