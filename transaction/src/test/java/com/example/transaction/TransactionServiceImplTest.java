package com.example.transaction;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionTypeEnum;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.daos.PutTransactionDao;
import com.example.transaction.models.entities.TransactionEntity;
import com.example.transaction.repositories.TransactionRepository;
import com.example.transaction.services.TransactionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testCreateTransaction() {
        // Arrange
        PostTransactionBin transactionBin = PostTransactionBin.builder()
                .type(TransactionTypeEnum.BOUGHT.getPersistedValue())
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .portfolioId(1)
                .currency("USD")
                .build();

        TransactionEntity savedEntity = TransactionEntity.builder()
                .id(1L)
                .type(TransactionTypeEnum.BOUGHT)
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .portfolioId(1)
                .currency("USD")
                .build();

        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);

        // Act
        Transaction createdTransaction = transactionService.createTransaction(transactionBin);

        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

        // Assert
        assertNotNull(createdTransaction);
        assertEquals(savedEntity.getId(), createdTransaction.getId());
        assertEquals(savedEntity.getType(), createdTransaction.getType());
        assertEquals(savedEntity.getDate(), createdTransaction.getDate());
        assertEquals(savedEntity.getAmount(), createdTransaction.getAmount());
        assertEquals(savedEntity.getPrice(), createdTransaction.getPrice());
        assertEquals(savedEntity.getSymbolId(), createdTransaction.getSymbolId());
        assertEquals(savedEntity.getCurrency(), createdTransaction.getCurrency());

    }

    @Test
    void testGetAllTransactionsByPortfolioId() {
        // Arrange
        long portfolioId = 1L;

        List<TransactionEntity> entities = new ArrayList<>();
        entities.add(TransactionEntity.builder()
                .id(1L)
                .type(TransactionTypeEnum.BOUGHT)
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .portfolioId(1)
                .currency("USD")
                .build());

        entities.add(TransactionEntity.builder()
                .id(2L)
                .type(TransactionTypeEnum.SOLD)
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
            assertEquals(entity.getType(), transaction.getType());
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
                .type(TransactionTypeEnum.BOUGHT)
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
        assertEquals(entity.getType(), transaction.getType());
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

        Transaction transaction = Transaction.builder()
                .id(1L)
                .type(TransactionTypeEnum.BOUGHT)
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .portfolioId(1)
                .currency("USD")
                .build();

        PutTransactionBin transactionBin = PutTransactionBin.builder()
                .id(1L)
                .transaction(
                        PutTransactionDao.builder()
                                .type(TransactionTypeEnum.BOUGHT.getPersistedValue())
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
                .type(TransactionTypeEnum.BOUGHT)
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .portfolioId(1)
                .currency("USD")
                .build();

        when(transactionRepository.existsById(transaction.getId())).thenReturn(true);

        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);

        // Act
        Transaction updatedTransaction = transactionService.updateTransaction(transactionBin);

        // Assert
        assertNotNull(updatedTransaction);
        assertEquals(transaction.getId(), updatedTransaction.getId());
        assertEquals(transaction.getType(), updatedTransaction.getType());
        assertEquals(transaction.getDate(), updatedTransaction.getDate());
        assertEquals(transaction.getAmount(), updatedTransaction.getAmount());
        assertEquals(transaction.getPrice(), updatedTransaction.getPrice());
        assertEquals(transaction.getSymbolId(), updatedTransaction.getSymbolId());
        assertEquals(transaction.getPortfolioId(), updatedTransaction.getPortfolioId());
        assertEquals(transaction.getCurrency(), updatedTransaction.getCurrency());

        verify(transactionRepository, times(1)).existsById(transaction.getId());

        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

    }

    @Test
    void testUpdateTransactionNotFound() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .type(TransactionTypeEnum.BOUGHT)
                .date(LocalDate.parse("2022-01-01"))
                .amount(10)
                .price(BigDecimal.valueOf(100))
                .symbolId("AAPL")
                .currency("USD")
                .build();

        PutTransactionBin transactionBin = PutTransactionBin.builder()
                .id(1L)
                .transaction(
                        PutTransactionDao.builder()
                                .type(TransactionTypeEnum.BOUGHT.getPersistedValue())
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

}