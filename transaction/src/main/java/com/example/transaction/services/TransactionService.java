package com.example.transaction.services;

import java.io.IOException;
import java.util.List;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionAfterDateBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;

public interface TransactionService {

    Transaction getTransactionById(long id) throws CustomException;

    List<Transaction> getAllTransactionsByPortfolioId(long portfolioId);

    Transaction updateTransaction(PutTransactionBin transactionBin) throws CustomException;

    void deleteTransaction(long id);

    List<Transaction> saveTransactionsFromCsv(UploadBin bin) throws CustomException, IOException;

    List<Transaction> getTransactionsByPortfolioIdAndDate(GetTransactionAfterDateBin bin);

    List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioId(long portfolioId);

}
