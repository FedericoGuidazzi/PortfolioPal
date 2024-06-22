package com.example.transaction.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.example.transaction.models.bin.GetTransactionAfterDateBin;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.dtos.PutTransactionDto;
import com.example.transaction.models.enums.TransactionType;
import com.example.transaction.services.RabbitMqSender;
import com.example.transaction.services.TransactionService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private RabbitMqSender sender;

	@GetMapping("/get/{portfolioId}")
	public ResponseEntity<List<Transaction>> getAllTransactionsByPortfolioId(@PathVariable long portfolioId,
			@RequestParam(defaultValue = "false") boolean mock) {
		if (mock) {
			return ResponseEntity.ok(List.of(
					Transaction.builder()
							.id(1)
							.type(TransactionType.BUY)
							.date(LocalDate.parse("2021-01-01"))
							.amount(100)
							.price(BigDecimal.valueOf(100))
							.symbolId("AAPL")
							.portfolioId(portfolioId)
							.currency("USD")
							.build(),
					Transaction.builder()
							.id(2)
							.type(TransactionType.BUY)
							.date(LocalDate.parse("2021-01-02"))
							.amount(100)
							.price(BigDecimal.valueOf(100))
							.symbolId("AAPL")
							.portfolioId(portfolioId)
							.currency("USD")
							.build()));

		}
		return ResponseEntity.ok(transactionService.getAllTransactionsByPortfolioId(portfolioId));
	}

	@SneakyThrows
	@PutMapping("update/{id}")
	public ResponseEntity<Transaction> updateTransaction(@PathVariable String id,
			@RequestBody PutTransactionDto entity) {
		this.sender.send(PostTransactionBin.builder()
				.date(entity.getDate())
				.portfolioId(entity.getPortfolioId())
				.build());
		return ResponseEntity.ok(transactionService.updateTransaction(
				PutTransactionBin.builder()
						.id(Long.parseLong(id))
						.transaction(entity)
						.build()));
	}

	@PutMapping("/delete/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
		try {
			Transaction transaction = transactionService.getTransactionById(Long.parseLong(id));
			this.sender
					.send(PostTransactionBin.builder()
							.date(transaction.getDate())
							.portfolioId(transaction.getPortfolioId())
							.build());
		} catch (NumberFormatException | CustomException e) {
		}
		transactionService.deleteTransaction(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}

	@SneakyThrows
	@PostMapping("/upload")
	public ResponseEntity<List<Transaction>> uploadFile(
			@RequestParam(value = "file", required = true) MultipartFile file) {
		if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
			throw new CustomException("Please upload a valid CSV file.");
		}
		List<Transaction> list = transactionService.saveTransactionsFromCsv(file.getInputStream());
		list.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
		this.sender.send(PostTransactionBin.builder()
				.date(list.get(list.size() - 1).getDate())
				.portfolioId(list.get(list.size() - 1).getPortfolioId())
				.build());
		return ResponseEntity.ok(list);
	}

	@GetMapping("/get/{portfolioId}/after_date")
	public ResponseEntity<List<Transaction>> getTransactionsByPortfolioIdAndDate(
			@PathVariable long portfolioId,
			@RequestParam String date, @RequestParam(defaultValue = "false") boolean mock) {

		if (mock) {
			return ResponseEntity.ok(List.of(Transaction.builder()
					.id(1)
					.type(TransactionType.BUY)
					.date(LocalDate.now())
					.amount(100)
					.price(BigDecimal.valueOf(100))
					.symbolId("AAPL")
					.portfolioId(portfolioId)
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
}
