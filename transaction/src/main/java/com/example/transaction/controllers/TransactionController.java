package com.example.transaction.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.transaction.PublicEndpoint;
import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionAfterDateBin;
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
	@PutMapping("update/{id}")
	public ResponseEntity<Transaction> updateTransaction(@PathVariable long id, @RequestHeader("X-Authenticated-UserId") String uid,
			@RequestBody PutTransactionDto entity) {

		Transaction transaction = transactionService.updateTransaction(
				PutTransactionBin.builder()
						.id(id)
						.userId(uid)
						.transaction(entity)
						.build());

		this.sender.send(PostTransactionBin.builder()
				.date(entity.getDate())
				.portfolioId(entity.getPortfolioId())
				.build());

		return ResponseEntity.ok(transaction);
	}

	@SneakyThrows
	@PutMapping("/delete/{id}")
	public ResponseEntity<Void> deleteTransaction(@RequestParam long portfolioId, @PathVariable long id, @RequestHeader("X-Authenticated-UserId") String uid) {
		Transaction deletedTransaction = Optional.ofNullable(transactionService.deleteTransaction(id, uid)).orElseThrow(() -> new CustomException("Transaction not found"));
			this.sender
					.send(PostTransactionBin.builder()
							.date(deletedTransaction.getDate())
							.portfolioId(portfolioId)
							.build());

		return ResponseEntity.ok().build();
	}

	@SneakyThrows
	@PostMapping("/upload/{portfolioId}")
	public ResponseEntity<List<Transaction>> uploadFile(
			@PathVariable long portfolioId,
			@RequestHeader("X-Authenticated-UserId") String uid,
			@RequestParam(value = "file", required = true) MultipartFile file) {
		if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
			throw new CustomException("Please upload a valid CSV file.");
		}
		List<Transaction> list = transactionService.saveTransactionsFromCsv(UploadBin.builder()
				.portfolioId(portfolioId)
				.inputStream(file.getInputStream())
				.userId(uid)
				.build());
		list.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
		this.sender.send(PostTransactionBin.builder()
				.date(list.get(list.size() - 1).getDate())
				.portfolioId(portfolioId)
				.build());
		return ResponseEntity.ok(list);
	}

	@PublicEndpoint
	@GetMapping("/get-by-portfolio/{portfolioId}")
	public ResponseEntity<List<Transaction>> getAllTransactionsByPortfolioId(@PathVariable long portfolioId,
			@RequestParam(defaultValue = "false") boolean mock) {
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
							.build(),
					Transaction.builder()
							.id(2)
							.type(TransactionType.BUY.getPersistedValue())
							.date(LocalDate.parse("2021-01-02"))
							.amount(100)
							.price(BigDecimal.valueOf(100))
							.symbolId("AAPL")
							.currency("USD")
							.build()));

		}
		return ResponseEntity.ok(transactionService.getAllTransactionsByPortfolioId(portfolioId));
	}

	@PublicEndpoint
	@GetMapping("/get-by-portfolio/{portfolioId}/after-date")
	public ResponseEntity<List<Transaction>> getTransactionsByPortfolioIdAndDate(
			@PathVariable long portfolioId,
			@RequestParam String date, @RequestParam(defaultValue = "false") boolean mock) {

		if (mock) {
			return ResponseEntity.ok(List.of(Transaction.builder()
					.id(1)
					.type(TransactionType.BUY.getPersistedValue())
					.date(LocalDate.now())
					.amount(100)
					.price(BigDecimal.valueOf(100))
					.symbolId("AAPL")
					.currency("USD")
					.build()));
		}

		LocalDate localDate = LocalDate.parse(date);
		List<Transaction> transactions = transactionService
				.getTransactionsByPortfolioIdAndDate(GetTransactionAfterDateBin.builder()
						.portfolioId(portfolioId)
						.date(localDate)
						.build());
		return ResponseEntity.ok(transactions);
	}

	@PublicEndpoint
	@GetMapping("/get-by-portfolio/{portfolioId}/assets-qty")
	public ResponseEntity<List<GetAssetQtyOutputBin>> getAssetsQtyByPortfolioId(@PathVariable long portfolioId,
			@RequestParam(defaultValue = "false") boolean mock) {
		if (mock) {
			return ResponseEntity.ok(List.of(GetAssetQtyOutputBin.builder()
					.symbolId("AAPL")
					.amount(100)
					.build()));
		}
		return ResponseEntity.ok(transactionService.getAssetsQtyByPortfolioId(portfolioId));
	}
}
