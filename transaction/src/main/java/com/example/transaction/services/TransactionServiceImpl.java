package com.example.transaction.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.CsvPortfolioReader;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionTypeEnum;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.entities.TransactionEntity;
import com.example.transaction.repositories.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(PostTransactionBin transactionBin) {
        TransactionEntity entity = transactionRepository.save(TransactionEntity.builder()
                .type(TransactionTypeEnum.fromValue(transactionBin.getType()))
                .date(transactionBin.getDate())
                .amount(transactionBin.getAmount())
                .price(transactionBin.getPrice())
                .symbolId(transactionBin.getSymbolId())
                .portfolioId(transactionBin.getPortfolioId())
                .currency(transactionBin.getCurrency())
                .build());

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
    public Transaction updateTransaction(Transaction transaction) throws CustomException {
        TransactionEntity entity = transactionRepository.findById(transaction.getId())
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
    public void deleteTransaction(long id) {
        transactionRepository.deleteById(id);
    }

}
