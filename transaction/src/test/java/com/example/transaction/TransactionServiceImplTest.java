package com.example.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;
import com.example.transaction.models.dtos.PutTransactionDto;
import com.example.transaction.models.entities.TransactionEntity;
import com.example.transaction.models.enums.TransactionType;
import com.example.transaction.repositories.TransactionRepository;
import com.example.transaction.services.CsvPortfolioReader;
import com.example.transaction.services.TransactionServiceImpl;

class TransactionServiceImplTest {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllTransactionsByPortfolioId() {
		// Arrange
		long portfolioId = 1L;

		List<TransactionEntity> entities = new ArrayList<>();
		entities.add(TransactionEntity.builder()
				.id(1L)
				.type(TransactionType.BUY)
				.date(LocalDate.parse("2022-01-01"))
				.amount(10)
				.price(BigDecimal.valueOf(100))
				.symbolId("AAPL")
				.portfolioId(1)
				.currency("USD")
				.build());

		entities.add(TransactionEntity.builder()
				.id(2L)
				.type(TransactionType.SELL)
				.date(LocalDate.parse("2022-01-01"))
				.amount(10)
				.price(BigDecimal.valueOf(100))
				.symbolId("AAPL")
				.portfolioId(1)
				.currency("USD")
				.build());

		when(transactionRepository.findAllByPortfolioId(portfolioId)).thenReturn(entities);

		// Act
		List<Transaction> transactions = transactionService.getAllTransactionsByPortfolioId(portfolioId);

		// Assert
		assertNotNull(transactions);
		assertEquals(entities.size(), transactions.size());

		for (int i = 0; i < entities.size(); i++) {
			TransactionEntity entity = entities.get(i);
			Transaction transaction = transactions.get(i);

			assertEquals(entity.getId(), transaction.getId());
			assertEquals(entity.getType().getPersistedValue(), transaction.getType());
			assertEquals(entity.getDate(), transaction.getDate());
			assertEquals(entity.getAmount(), transaction.getAmount());
			assertEquals(entity.getPrice(), transaction.getPrice());
			assertEquals(entity.getSymbolId(), transaction.getSymbolId());
			assertEquals(entity.getCurrency(), transaction.getCurrency());
		}

		verify(transactionRepository, times(1)).findAllByPortfolioId(portfolioId);
	}

	@Test
	void testGetTransactionById() throws CustomException {
		// Arrange
		long id = 1L;

		TransactionEntity entity = TransactionEntity.builder()
				.id(1L)
				.type(TransactionType.BUY)
				.date(LocalDate.parse("2022-01-01"))
				.amount(10)
				.price(BigDecimal.valueOf(100))
				.symbolId("AAPL")
				.portfolioId(1)
				.currency("USD")
				.build();

		when(transactionRepository.findById(id)).thenReturn(Optional.of(entity));

		// Act
		Transaction transaction = transactionService.getTransactionById(id);

		// Assert
		assertNotNull(transaction);
		assertEquals(entity.getId(), transaction.getId());
		assertEquals(entity.getType().getPersistedValue(), transaction.getType());
		assertEquals(entity.getDate(), transaction.getDate());
		assertEquals(entity.getAmount(), transaction.getAmount());
		assertEquals(entity.getPrice(), transaction.getPrice());
		assertEquals(entity.getSymbolId(), transaction.getSymbolId());
		assertEquals(entity.getCurrency(), transaction.getCurrency());

		verify(transactionRepository, times(1)).findById(id);
	}

	@Test
	void testGetTransactionByIdNotFound() {
		// Arrange
		long id = 1L;

		when(transactionRepository.findById(id)).thenReturn(Optional.empty());

		// Act and Assert
		assertThrows(CustomException.class, () -> transactionService.getTransactionById(id));

		verify(transactionRepository, times(1)).findById(id);
	}

	@Test
	void testUpdateTransaction() throws CustomException {

		PutTransactionBin transactionBin = PutTransactionBin.builder()
				.id(1L)
				.transaction(
						PutTransactionDto.builder()
								.type(TransactionType.BUY.getPersistedValue())
								.date(LocalDate.parse("2022-01-01"))
								.amount(10)
								.price(BigDecimal.valueOf(100))
								.symbolId("AAPL")
								.portfolioId(1)
								.currency("USD")
								.build())
				.build();

		TransactionEntity savedEntity = TransactionEntity.builder()
				.id(1L)
				.type(TransactionType.BUY)
				.date(LocalDate.parse("2022-01-01"))
				.amount(10)
				.price(BigDecimal.valueOf(100))
				.symbolId("AAPL")
				.portfolioId(1)
				.currency("USD")
				.build();

		when(transactionRepository.existsById(transactionBin.getId())).thenReturn(true);

		when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);

		// Act
		Transaction updatedTransaction = transactionService.updateTransaction(transactionBin);

		// Assert
		assertNotNull(updatedTransaction);
		assertEquals(transactionBin.getId(), updatedTransaction.getId());
		assertEquals(transactionBin.getTransaction().getType(), updatedTransaction.getType());
		assertEquals(transactionBin.getTransaction().getDate(), updatedTransaction.getDate());
		assertEquals(transactionBin.getTransaction().getAmount(), updatedTransaction.getAmount());
		assertEquals(transactionBin.getTransaction().getPrice(), updatedTransaction.getPrice());
		assertEquals(transactionBin.getTransaction().getSymbolId(), updatedTransaction.getSymbolId());
		assertEquals(transactionBin.getTransaction().getCurrency(), updatedTransaction.getCurrency());

		verify(transactionRepository, times(1)).existsById(transactionBin.getId());

		verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

	}

	@Test
	void testUpdateTransactionNotFound() {
		// Arrange
		Transaction transaction = Transaction.builder()
				.id(1L)
				.type(TransactionType.BUY.getPersistedValue())
				.date(LocalDate.parse("2022-01-01"))
				.amount(10)
				.price(BigDecimal.valueOf(100))
				.symbolId("AAPL")
				.currency("USD")
				.build();

		PutTransactionBin transactionBin = PutTransactionBin.builder()
				.id(1L)
				.transaction(
						PutTransactionDto.builder()
								.type(TransactionType.BUY.getPersistedValue())
								.date(LocalDate.parse("2022-01-01"))
								.amount(10)
								.price(BigDecimal.valueOf(100))
								.symbolId("AAPL")
								.portfolioId(1)
								.currency("USD")
								.build())
				.build();

		when(transactionRepository.existsById(transaction.getId())).thenReturn(false);

		// Act and Assert
		assertThrows(CustomException.class, () -> transactionService.updateTransaction(transactionBin));

		verify(transactionRepository, times(1)).existsById(transaction.getId());

		verify(transactionRepository, times(0)).save(any(TransactionEntity.class));

	}

	@Test
	public void testReadCsvFile() throws Exception {
		String csvData = "date,type,amount,symbolId,price,portfolioId,currency\n" +
				"2021-01-01,Acquisto,10,AAPL,100.0,1,USD\n" +
				"2021-01-01,Acquisto,10,GOOGL,100.0,1,USD\n" +
				"2021-02-01,Vendita,5,GOOGL,200.0,1,USD";

		InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

		when(transactionRepository.saveAll(anyIterable())).thenReturn(null);

		List<Transaction> transactions = transactionService.saveTransactionsFromCsv(
				UploadBin.builder()
						.inputStream(inputStream)
						.portfolioId(1)
						.build());

		assertEquals(3, transactions.size());

		Transaction firstTransaction = transactions.get(0);
		assertEquals(LocalDate.of(2021, 1, 1), firstTransaction.getDate());
		assertEquals(TransactionType.BUY.getPersistedValue(), firstTransaction.getType());
		assertEquals(10.0, firstTransaction.getAmount());
		assertEquals("AAPL", firstTransaction.getSymbolId());
		assertEquals(BigDecimal.valueOf(100.0), firstTransaction.getPrice());
		assertEquals("USD", firstTransaction.getCurrency());

		Transaction secondTransaction = transactions.get(1);
		assertEquals(LocalDate.of(2021, 1, 1), secondTransaction.getDate());
		assertEquals(TransactionType.BUY.getPersistedValue(), secondTransaction.getType());
		assertEquals(10.0, secondTransaction.getAmount());
		assertEquals("GOOGL", secondTransaction.getSymbolId());
		assertEquals(BigDecimal.valueOf(100.0), secondTransaction.getPrice());
		assertEquals("USD", secondTransaction.getCurrency());
	}

	@Test
	void testGetTransactionsByPortfolioIdAfterDate() {
		// Mock the repository call
		TransactionEntity transactionEntity1 = TransactionEntity.builder()
				.id(1L)
				.type(TransactionType.BUY)
				.date(LocalDate.of(2021, 1, 1))
				.amount(100.0)
				.price(BigDecimal.valueOf(50.0))
				.symbolId("AAPL")
				.currency("USD")
				.build();

		TransactionEntity transactionEntity2 = TransactionEntity.builder()
				.id(2L)
				.type(TransactionType.SELL)
				.date(LocalDate.of(2021, 2, 1))
				.amount(50.0)
				.price(BigDecimal.valueOf(60.0))
				.symbolId("GOOGL")
				.currency("USD")
				.build();

		when(transactionRepository.findAllByPortfolioIdAndAfterDate(anyLong(), any(LocalDate.class)))
				.thenReturn(Arrays.asList(transactionEntity1, transactionEntity2));

		// Call the service method
		List<Transaction> transactions = transactionService.getTransactionsByPortfolioIdAfterDate(
				GetTransactionByDateBin.builder()
						.portfolioId(1L)
						.date(LocalDate.of(2021, 1, 1))
						.build());

		// Verify the result
		assertEquals(2, transactions.size());

		Transaction transaction1 = transactions.get(0);
		assertEquals(1L, transaction1.getId());
		assertEquals(TransactionType.BUY.getPersistedValue(), transaction1.getType());
		assertEquals(LocalDate.of(2021, 1, 1), transaction1.getDate());
		assertEquals(100.0, transaction1.getAmount());
		assertEquals(BigDecimal.valueOf(50.0), transaction1.getPrice());
		assertEquals("AAPL", transaction1.getSymbolId());
		assertEquals("USD", transaction1.getCurrency());

		Transaction transaction2 = transactions.get(1);
		assertEquals(2L, transaction2.getId());
		assertEquals(TransactionType.SELL.getPersistedValue(), transaction2.getType());
		assertEquals(LocalDate.of(2021, 2, 1), transaction2.getDate());
		assertEquals(50.0, transaction2.getAmount());
		assertEquals(BigDecimal.valueOf(60.0), transaction2.getPrice());
		assertEquals("GOOGL", transaction2.getSymbolId());
		assertEquals("USD", transaction2.getCurrency());
	}

	@Test
	void testGetAssetsQtyByPortfolioIdAndDate() {
		// Mock the repository call
		GetAssetQtyOutputBin assetQty1 = GetAssetQtyOutputBin.builder()
				.symbolId("AAPL")
				.amount(100)
				.build();

		GetAssetQtyOutputBin assetQty2 = GetAssetQtyOutputBin.builder()
				.symbolId("GOOGL")
				.amount(50)
				.build();

		when(transactionRepository.findAssetsQtyByPortfolioIdAndDate(anyLong(), any(LocalDate.class)))
				.thenReturn(Arrays.asList(assetQty1, assetQty2));

		// Call the method to test
		List<GetAssetQtyOutputBin> result = transactionService.getAssetsQtyByPortfolioIdAndDate(
				GetTransactionByDateBin.builder()
						.portfolioId(1L)
						.date(LocalDate.of(2021, 1, 1))
						.build());

		// Verify the results
		assertEquals(2, result.size());
		assertEquals("AAPL", result.get(0).getSymbolId());
		assertEquals(100, result.get(0).getAmount());
		assertEquals("GOOGL", result.get(1).getSymbolId());
		assertEquals(50, result.get(1).getAmount());
	}

	@Test
	void testGetAssetsQtyByPortfolioId() {
		// Mock the repository call
		GetAssetQtyOutputBin assetQty1 = GetAssetQtyOutputBin.builder()
				.symbolId("AAPL")
				.amount(100)
				.build();

		GetAssetQtyOutputBin assetQty2 = GetAssetQtyOutputBin.builder()
				.symbolId("GOOGL")
				.amount(50)
				.build();

		when(transactionRepository.findAssetsQtyByPortfolioIdAndDate(anyLong(), any(LocalDate.class)))
				.thenReturn(Arrays.asList(assetQty1, assetQty2));

		// Call the method to test
		List<GetAssetQtyOutputBin> result = transactionService.getAssetsQtyByPortfolioId(1L);

		// Verify the results
		assertEquals(2, result.size());
		assertEquals("AAPL", result.get(0).getSymbolId());
		assertEquals(100, result.get(0).getAmount());
		assertEquals("GOOGL", result.get(1).getSymbolId());
		assertEquals(50, result.get(1).getAmount());
	}

	@Test
	void testGetTransactionsByPortfolioIdAndSymbolId() {
		// Configurazione del mock
		TransactionEntity transaction1 = Instancio.create(TransactionEntity.class);
		transaction1.setPortfolioId(1L);
		transaction1.setSymbolId("AAPL");

		TransactionEntity transaction2 = Instancio.create(TransactionEntity.class);
		transaction2.setPortfolioId(1L);
		transaction2.setSymbolId("AAPL");

		List<TransactionEntity> entities = List.of(transaction1, transaction2);
		when(transactionRepository.findAllByPortfolioIdAndSymbolId(anyLong(), anyString())).thenReturn(entities);

		// Chiamata al metodo da testare
		List<Transaction> transactions = transactionService.getTransactionsByPortfolioIdAndSymbolId(1L, "AAPL");

		// Verifica del risultato
		assertNotNull(transactions);
		assertEquals(2, transactions.size());

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionRepository).findAllByPortfolioIdAndSymbolId(1L, "AAPL");
	}

	@Test
	void testInsertTransaction() throws CustomException {
		// Configurazione del mock
		PostTransactionBin transactionBin = Instancio.create(PostTransactionBin.class);
		transactionBin.setType(TransactionType.BUY.getPersistedValue());
		transactionBin.setDate(LocalDate.parse("2021-01-01"));

		TransactionEntity savedEntity = Instancio.create(TransactionEntity.class);

		when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);

		// Chiamata al metodo da testare
		Transaction transaction = transactionService.insertTransaction(transactionBin);

		// Verifica del risultato
		assertNotNull(transaction);

		// Verifica che il mock sia stato chiamato correttamente
		verify(transactionRepository).save(any(TransactionEntity.class));
	}

	@Test
	void testInsertTransactionInvalidType() {
		// Configurazione del mock
		PostTransactionBin transactionBin = Instancio.create(PostTransactionBin.class);
		transactionBin.setType("InvalidType");

		// Verifica che venga lanciata l'eccezione
		CustomException exception = assertThrows(CustomException.class, () -> {
			transactionService.insertTransaction(transactionBin);
		});

		// Verifica del messaggio dell'eccezione
		assertEquals("Invalid transaction type", exception.getMessage());
	}

}