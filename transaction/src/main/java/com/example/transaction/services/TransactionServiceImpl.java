package com.example.transaction.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.CsvPortfolioReader;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetTransactionAfterDateBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.entities.TransactionEntity;
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
						.type(entity.getType())
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
				.type(entity.getType())
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
				.type(transactionBin.getTransaction().getType())
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
				.portfolioId(entity.getPortfolioId())
				.currency(entity.getCurrency())
				.build();
	}

	@Override
	public void deleteTransaction(long id) {
		transactionRepository.delete(TransactionEntity.builder().id(id).build());
	}

	@Override
	public List<Transaction> saveTransactionsFromCsv(InputStream inputStream) throws CustomException, IOException {
		List<Transaction> transactions = CsvPortfolioReader.readCsvFile(inputStream);
		transactionRepository.saveAll(transactions.stream()
				.map(entity -> TransactionEntity.builder()
						.id(entity.getId())
						.type(entity.getType())
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.portfolioId(entity.getPortfolioId())
						.currency(entity.getCurrency())
						.build())
				.toList());

		return transactions;
	}

	@Override
	public List<Transaction> getTransactionsByPortfolioIdAndDate(GetTransactionAfterDateBin bin) {
		List<TransactionEntity> list = transactionRepository.findAllByPortfolioIdAndDateAfter(bin.getPortfolioId(),
				bin.getDate());

		return list.stream()
				.map(entity -> Transaction.builder()
						.id(entity.getId())
						.type(entity.getType())
						.date(entity.getDate())
						.amount(entity.getAmount())
						.price(entity.getPrice())
						.symbolId(entity.getSymbolId())
						.currency(entity.getCurrency())
						.build())
				.toList();
	}

}
