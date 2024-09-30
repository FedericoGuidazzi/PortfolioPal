package com.example.transaction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.transaction.controllers.TransactionController;
import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;
import com.example.transaction.models.dtos.PutTransactionDto;
import com.example.transaction.services.RabbitMqSender;
import com.example.transaction.services.TransactionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

	@Mock
	private TransactionService transactionService;

	@Mock
	private RabbitMqSender sender;

	@InjectMocks
	private TransactionController transactionController;

	@BeforeEach
	void setUp() {
		// Inizializzazione se necessaria
	}

	@Test
	void testUpdateTransactionWithPastDate() {
		// Configurazione del mock
		PutTransactionDto putTransactionDto = Instancio.create(PutTransactionDto.class);
		putTransactionDto.setDate(LocalDate.now().minusDays(1));
		putTransactionDto.setOldDate(LocalDate.now());
		PutTransactionBin putTransactionBin = PutTransactionBin.builder()
				.id(1)
				.transaction(putTransactionDto)
				.build();

		Transaction mockTransaction = Transaction.builder()
				.id(1)
				.date(putTransactionDto.getDate())
				.portfolioId(putTransactionDto.getPortfolioId())
				.price(putTransactionDto.getPrice())
				.symbolId(putTransactionDto.getSymbolId())
				.amount(putTransactionDto.getAmount())
				.type(putTransactionDto.getType())
				.currency(putTransactionDto.getCurrency())
				.build();
		when(transactionService.updateTransaction(putTransactionBin)).thenReturn(mockTransaction);

		// Chiamata al metodo da testare
		ResponseEntity<Transaction> result = transactionController.updateTransaction(1, putTransactionDto);

		// Verifica del risultato
		assertNotNull(result);
		assertEquals(mockTransaction, result.getBody());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).updateTransaction(putTransactionBin);
		verify(sender).sendTransactionUpdate(GetTransactionByDateBin.builder()
				.date(mockTransaction.getDate())
				.portfolioId(mockTransaction.getPortfolioId())
				.build());
	}

	@Test
	void testUpdateTransactionWithCurrentOrFutureDate() {
		// Configurazione del mock
		PutTransactionDto putTransactionDto = Instancio.create(PutTransactionDto.class);
		putTransactionDto.setDate(LocalDate.now());
		putTransactionDto.setOldDate(LocalDate.now());
		PutTransactionBin putTransactionBin = PutTransactionBin.builder()
				.id(1)
				.transaction(putTransactionDto)
				.build();

		Transaction mockTransaction = Transaction.builder()
				.id(1)
				.date(putTransactionDto.getDate())
				.portfolioId(putTransactionDto.getPortfolioId())
				.price(putTransactionDto.getPrice())
				.symbolId(putTransactionDto.getSymbolId())
				.amount(putTransactionDto.getAmount())
				.type(putTransactionDto.getType())
				.currency(putTransactionDto.getCurrency())
				.build();
		when(transactionService.updateTransaction(putTransactionBin)).thenReturn(mockTransaction);

		// Chiamata al metodo da testare
		ResponseEntity<Transaction> result = transactionController.updateTransaction(1, putTransactionDto);

		// Verifica del risultato
		assertNotNull(result);
		assertEquals(mockTransaction, result.getBody());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).updateTransaction(putTransactionBin);
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testDeleteTransactionWithPastDate() {
		// Configurazione del mock
		Transaction deletedTransaction = Instancio.create(Transaction.class);
		deletedTransaction.setDate(LocalDate.now().minusDays(1));

		when(transactionService.deleteTransaction(anyLong())).thenReturn(deletedTransaction);

		// Chiamata al metodo da testare
		ResponseEntity<Void> response = transactionController.deleteTransaction(deletedTransaction.getId(),
				deletedTransaction.getPortfolioId());

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(ResponseEntity.ok().build(), response);

		// Verifica che il mock sia stato chiamato correttamente
		verify(sender).sendTransactionUpdate(GetTransactionByDateBin.builder()
				.date(deletedTransaction.getDate())
				.portfolioId(deletedTransaction.getPortfolioId())
				.build());
	}

	@Test
	void testDeleteTransactionWithCurrentOrFutureDate() {
		// Configurazione del mock
		Transaction deletedTransaction = Instancio.create(Transaction.class);
		deletedTransaction.setDate(LocalDate.now());

		when(transactionService.deleteTransaction(anyLong())).thenReturn(deletedTransaction);

		// Chiamata al metodo da testare
		ResponseEntity<Void> response = transactionController.deleteTransaction(1L, 123L);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(ResponseEntity.ok().build(), response);

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).deleteTransaction(1L);
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testDeleteTransactionNotFound() {
		// Configurazione del mock per lanciare un'eccezione
		when(transactionService.deleteTransaction(1L)).thenReturn(null);

		// Verifica che venga lanciata l'eccezione
		assertThrows(CustomException.class, () -> {
			transactionController.deleteTransaction(1L, 123L);
		});

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).deleteTransaction(1L);
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testInsertTransactionWithPastDate() {
		// Configurazione del mock
		PostTransactionBin entity = Instancio.create(PostTransactionBin.class);
		entity.setDate(LocalDate.now().minusDays(1));

		Transaction transaction = Instancio.create(Transaction.class);
		transaction.setDate(LocalDate.now().minusDays(1));

		when(transactionService.insertTransaction(entity)).thenReturn(transaction);

		// Chiamata al metodo da testare
		ResponseEntity<Transaction> response = transactionController.insertTransaction(entity);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(ResponseEntity.ok(transaction), response);

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).insertTransaction(entity);
		verify(sender).sendTransactionUpdate(GetTransactionByDateBin.builder()
				.date(transaction.getDate())
				.portfolioId(entity.getPortfolioId())
				.build());
	}

	@Test
	void testInsertTransactionWithCurrentOrFutureDate() {
		// Configurazione del mock
		PostTransactionBin entity = Instancio.create(PostTransactionBin.class);
		entity.setDate(LocalDate.now());

		Transaction transaction = Instancio.create(Transaction.class);
		transaction.setDate(LocalDate.now());

		when(transactionService.insertTransaction(entity)).thenReturn(transaction);

		// Chiamata al metodo da testare
		ResponseEntity<Transaction> response = transactionController.insertTransaction(entity);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(ResponseEntity.ok(transaction), response);

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).insertTransaction(entity);
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testUploadFileWithValidCsv() throws Exception {
		// Configurazione del mock
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getOriginalFilename()).thenReturn("transactions.csv");
		InputStream inputStream = new ByteArrayInputStream("test data".getBytes());
		when(file.getInputStream()).thenReturn(inputStream);

		Transaction transaction = Instancio.create(Transaction.class);
		transaction.setPortfolioId(123L);
		transaction.setDate(LocalDate.now());

		Transaction transaction2 = Instancio.create(Transaction.class);
		transaction2.setPortfolioId(123L);
		transaction2.setDate(LocalDate.now().plusDays(3));

		List<Transaction> transactions = List.of(transaction, transaction2);

		when(transactionService.saveTransactionsFromCsv(any(UploadBin.class))).thenReturn(transactions);

		// Chiamata al metodo da testare
		ResponseEntity<List<Transaction>> response = transactionController
				.uploadFile(transaction.getPortfolioId(), file);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(ResponseEntity.ok(transactions), response);

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).saveTransactionsFromCsv(any(UploadBin.class));
	}

	@Test
	void testUploadFileWithInvalidFile() throws CustomException, IOException {
		// Configurazione del mock
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(true);

		// Verifica che venga lanciata l'eccezione
		assertThrows(CustomException.class, () -> {
			transactionController.uploadFile(123L, file);
		});

		// Verifica che il mock non sia stato chiamato
		verify(transactionService, never()).saveTransactionsFromCsv(any());
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testUploadFileWithNonCsvFile() {
		// Configurazione del mock
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getOriginalFilename()).thenReturn("transactions.txt");

		// Verifica che venga lanciata l'eccezione
		assertThrows(CustomException.class, () -> {
			transactionController.uploadFile(123L, file);
		});

		// Verifica che il mock non sia stato chiamato
		try {
			verify(transactionService, never()).saveTransactionsFromCsv(any());
		} catch (CustomException | IOException e) {
			e.printStackTrace();
		}
		verify(sender, never()).sendTransactionUpdate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testGetAllTransactionsByPortfolioIdWithMock() {

		// Chiamata al metodo da testare con mock=true
		ResponseEntity<List<Transaction>> response = transactionController.getAllTransactionsByPortfolioId(123L, true,
				null);

		// Verifica del risultato
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().size());
		assertEquals(1, response.getBody().get(0).getId());
		assertEquals(2, response.getBody().get(1).getId());
	}

	@Test
	void testGetAllTransactionsByPortfolioIdWithoutDate() {
		// Configurazione del mock

		Transaction transaction = Instancio.create(Transaction.class);

		List<Transaction> transactions = List.of(
				transaction,
				transaction);
		when(transactionService.getAllTransactionsByPortfolioId(anyLong())).thenReturn(transactions);

		// Chiamata al metodo da testare con date=null
		ResponseEntity<List<Transaction>> response = transactionController
				.getAllTransactionsByPortfolioId(transaction.getPortfolioId(), false, null);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(transactions, response.getBody());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).getAllTransactionsByPortfolioId(transaction.getPortfolioId());
	}

	@Test
	void testGetAllTransactionsByPortfolioIdWithDate() {
		// Configurazione del mock

		Transaction transaction = Instancio.create(Transaction.class);

		List<Transaction> transactions = List.of(
				transaction,
				transaction);
		when(transactionService.getTransactionsByPortfolioIdAfterDate(any(GetTransactionByDateBin.class)))
				.thenReturn(transactions);

		// Chiamata al metodo da testare con una data specificata
		LocalDate date = LocalDate.parse(transaction.getDate().toString());
		ResponseEntity<List<Transaction>> response = transactionController
				.getAllTransactionsByPortfolioId(transaction.getPortfolioId(), false, date);

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(transactions, response.getBody());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).getTransactionsByPortfolioIdAfterDate(any(GetTransactionByDateBin.class));
	}

	@Test
	void testGetAssetsQtyByPortfolioIdWithMock() {
		long portfolioId = 1L;
		ResponseEntity<List<GetAssetQtyOutputBin>> response = transactionController
				.getAssetsQtyByPortfolioId(portfolioId, null, true);

		List<GetAssetQtyOutputBin> result = response.getBody();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("AAPL", result.get(0).getSymbolId());
		assertEquals(100, result.get(0).getAmount());
	}

	@Test
	void testGetAssetsQtyByPortfolioIdWithoutDate() {
		long portfolioId = 1L;
		List<GetAssetQtyOutputBin> expectedResult = List.of(new GetAssetQtyOutputBin("GOOGL", 50));
		when(transactionService.getAssetsQtyByPortfolioId(portfolioId)).thenReturn(expectedResult);

		ResponseEntity<List<GetAssetQtyOutputBin>> response = transactionController
				.getAssetsQtyByPortfolioId(portfolioId, null, false);

		assertEquals(expectedResult, response.getBody());
		verify(transactionService).getAssetsQtyByPortfolioId(portfolioId);
	}

	@Test
	void testGetAssetsQtyByPortfolioIdWithDate() {
		long portfolioId = 1L;
		LocalDate date = LocalDate.now();
		List<GetAssetQtyOutputBin> expectedResult = List.of(new GetAssetQtyOutputBin("MSFT", 75));
		when(transactionService.getAssetsQtyByPortfolioIdAndDate(any(GetTransactionByDateBin.class)))
				.thenReturn(expectedResult);

		ResponseEntity<List<GetAssetQtyOutputBin>> response = transactionController
				.getAssetsQtyByPortfolioId(portfolioId, date, false);

		assertEquals(expectedResult, response.getBody());
		verify(transactionService).getAssetsQtyByPortfolioIdAndDate(
				argThat(bin -> bin.getPortfolioId() == portfolioId && bin.getDate().equals(date)));
	}

	@Test
	void testGetTransactionsByPortfolioIdAndSymbolId() {
		// Configurazione del mock
		List<Transaction> transactions = List.of(
				Transaction.builder()
						.id(1)
						.type("BUY")
						.date(LocalDate.parse("2021-01-01"))
						.amount(100)
						.price(BigDecimal.valueOf(100))
						.symbolId("AAPL")
						.currency("USD")
						.portfolioId(123L)
						.build(),
				Transaction.builder()
						.id(2)
						.type("SELL")
						.date(LocalDate.parse("2021-01-02"))
						.amount(50)
						.price(BigDecimal.valueOf(150))
						.symbolId("AAPL")
						.currency("USD")
						.portfolioId(123L)
						.build());
		when(transactionService.getTransactionsByPortfolioIdAndSymbolId(123L, "AAPL")).thenReturn(transactions);

		// Chiamata al metodo da testare
		ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByPortfolioIdAndSymbolId(123L,
				"AAPL");

		// Verifica del risultato
		assertNotNull(response);
		assertEquals(transactions, response.getBody());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionService).getTransactionsByPortfolioIdAndSymbolId(123L, "AAPL");
	}

}