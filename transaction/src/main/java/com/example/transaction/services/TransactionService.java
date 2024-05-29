package com.example.transaction.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.PostTransactionBin;

public interface TransactionService {

    Transaction createTransaction(PostTransactionBin transactionBin);

    Transaction getTransactionById(long id) throws CustomException;

    List<Transaction> getAllTransactionsByPortfolioId(long portfolioId);

    Transaction updateTransaction(Transaction transaction) throws CustomException;

    void deleteTransaction(long id);

    List<Transaction> saveTransactionsFromCsv(InputStream inputStream) throws CustomException, IOException;

}
