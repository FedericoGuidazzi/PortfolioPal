package com.example.transaction.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;
import com.example.transaction.models.dtos.PutTransactionDto;
import com.example.transaction.models.enums.TransactionType;
import com.example.transaction.services.RabbitMqSender;
import com.example.transaction.services.TransactionService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private RabbitMqSender sender;

	@SneakyThrows
	@PutMapping("/update/{id}")
	public ResponseEntity<Transaction> updateTransaction(@PathVariable long id,
			@RequestBody PutTransactionDto entity) {

		Transaction transaction = transactionService.updateTransaction(
				PutTransactionBin.builder()
						.id(id)
						.transaction(entity)
						.build());

		LocalDate olderDate = entity.getDate();
		if (olderDate.isBefore(LocalDate.now())) {
			this.sender.sendTransactionUpdate(GetTransactionByDateBin.builder()
					.date(olderDate)
					.portfolioId(entity.getPortfolioId())
					.build());
		}

		return ResponseEntity.ok(transaction);
	}

	@SneakyThrows
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable long id, @RequestParam long portfolioId) {
		Transaction deletedTransaction = Optional.ofNullable(transactionService.deleteTransaction(id))
				.orElseThrow(() -> new CustomException("Transaction not found"));

		if (deletedTransaction.getDate().isBefore(LocalDate.now())) {
			this.sender
					.sendTransactionUpdate(GetTransactionByDateBin.builder()
							.date(deletedTransaction.getDate())
							.portfolioId(portfolioId)
							.build());
		}

		return ResponseEntity.ok().build();
	}

	@SneakyThrows
	@PostMapping("/insert")
	public ResponseEntity<Transaction> insertTransaction(@RequestBody PostTransactionBin entity) {
		Transaction transaction = transactionService.insertTransaction(entity);

		if (transaction.getDate().isBefore(LocalDate.now())) {
			this.sender.sendTransactionUpdate(GetTransactionByDateBin.builder()
					.date(transaction.getDate())
					.portfolioId(entity.getPortfolioId())
					.build());
		}

		return ResponseEntity.ok(transaction);
	}

	@SneakyThrows
	@PostMapping("/upload/{portfolioId}")
	public ResponseEntity<List<Transaction>> uploadFile(
			@PathVariable long portfolioId,
			@RequestParam(value = "file", required = true) MultipartFile file) {

		if (file.isEmpty() ||
				Optional.ofNullable(file.getOriginalFilename()).orElse("").endsWith(".csv") == false) {
			throw new CustomException("Please upload a valid CSV file.");
		}

		List<Transaction> list = transactionService.saveTransactionsFromCsv(UploadBin.builder()
				.portfolioId(portfolioId)
				.inputStream(file.getInputStream())
				.build());

		List<Transaction> oldTransactions = list.stream().filter(e -> !e.getDate().isEqual(LocalDate.now())).toList();

		oldTransactions = oldTransactions.stream()
				.sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
				.toList();

		this.sender.sendTransactionUpdate(GetTransactionByDateBin.builder()
				.date(oldTransactions.get(oldTransactions.size() - 1).getDate())
				.portfolioId(portfolioId)
				.build());

		return ResponseEntity.ok(list);
	}

	@GetMapping("/get-by-portfolio/{portfolioId}")
	public ResponseEntity<List<Transaction>> getAllTransactionsByPortfolioId(@PathVariable long portfolioId,
			@RequestParam(defaultValue = "false") boolean mock,
			@RequestParam(required = false) LocalDate date) {
		if (mock) {
			return ResponseEntity.ok(List.of(
					Transaction.builder()
							.id(1)
							.type(TransactionType.BUY.getPersistedValue())
							.date(LocalDate.parse("2021-01-01"))
							.amount(100)
							.price(BigDecimal.valueOf(100))
							.symbolId("AAPL")
							.currency("USD")
							.portfolioId(portfolioId)
							.build(),
					Transaction.builder()
							.id(2)
							.type(TransactionType.BUY.getPersistedValue())
							.date(LocalDate.parse("2021-01-02"))
							.amount(100)
							.price(BigDecimal.valueOf(100))
							.symbolId("AAPL")
							.currency("USD")
							.portfolioId(portfolioId)
							.build()));

		}

		return date == null
				? ResponseEntity.ok(transactionService.getAllTransactionsByPortfolioId(portfolioId))
				: ResponseEntity
						.ok(transactionService.getTransactionsByPortfolioIdAfterDate(GetTransactionByDateBin.builder()
								.portfolioId(portfolioId)
								.date(date)
								.build()));
	}

	@GetMapping("/get-by-portfolio/{portfolioId}/assets-qty")
	public ResponseEntity<List<GetAssetQtyOutputBin>> getAssetsQtyByPortfolioId(@PathVariable long portfolioId,
			@RequestParam(required = false) LocalDate date,
			@RequestParam(defaultValue = "false") boolean mock) {
		if (mock) {
			return ResponseEntity.ok(List.of(GetAssetQtyOutputBin.builder()
					.symbolId("AAPL")
					.amount(100)
					.build()));
		}
		return date == null
				? ResponseEntity.ok(transactionService.getAssetsQtyByPortfolioId(portfolioId))
				: ResponseEntity
						.ok(transactionService.getAssetsQtyByPortfolioIdAndDate(GetTransactionByDateBin.builder()
								.portfolioId(portfolioId)
								.date(date)
								.build()));
	}

	@GetMapping("/get-by-portfolio/{portfolioId}/asset/{symbolId}")
	public ResponseEntity<List<Transaction>> getTransactionsByPortfolioIdAndSymbolId(@PathVariable long portfolioId,
			@PathVariable String symbolId) {
		return ResponseEntity.ok(transactionService.getTransactionsByPortfolioIdAndSymbolId(portfolioId, symbolId));
	}
}
