package com.example.transaction.services;

import java.io.IOException;
import java.util.List;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.bin.GetTransactionByDateBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.bin.UploadBin;

/**
 * The TransactionService interface provides methods for managing transactions.
 */
public interface TransactionService {

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction
     * @return the transaction with the specified ID
     * @throws CustomException if an error occurs while retrieving the transaction
     */
    Transaction getTransactionById(long id) throws CustomException;

    /**
     * Retrieves all transactions associated with a portfolio.
     *
     * @param portfolioId the ID of the portfolio
     * @return a list of transactions associated with the portfolio
     */
    List<Transaction> getAllTransactionsByPortfolioId(long portfolioId);

    /**
     * Updates a transaction.
     *
     * @param transactionBin the updated transaction data
     * @return the updated transaction
     * @throws CustomException if an error occurs while updating the transaction
     */
    Transaction updateTransaction(PutTransactionBin transactionBin) throws CustomException;

    /**
     * Deletes a transaction.
     *
     * @param id  the ID of the transaction to delete
     * @return the deleted transaction
     */
    Transaction deleteTransaction(long id);

    /**
     * Saves transactions from a CSV file.
     *
     * @param bin the uploaded CSV file data
     * @return a list of saved transactions
     * @throws CustomException if an error occurs while saving the transactions
     * @throws IOException     if an I/O error occurs while reading the CSV file
     */
    List<Transaction> saveTransactionsFromCsv(UploadBin bin) throws CustomException, IOException;

    /**
     * Retrieves transactions associated with a portfolio and after a specified date.
     *
     * @param bin the input data containing the portfolio ID and date
     * @return a list of transactions matching the criteria
     */
    List<Transaction> getTransactionsByPortfolioIdAfterDate(GetTransactionByDateBin bin);

    /**
     * Retrieves asset quantities associated with a portfolio.
     *
     * @param portfolioId the ID of the portfolio
     * @return a list of asset quantities associated with the portfolio
     */
    List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioId(long portfolioId);

    /**
     * Retrieves asset quantities associated with a portfolio and in a specified date.
     *
     * @param bin the input data containing the portfolio ID and date
     * @return a list of asset quantities associated with the portfolio
     */
    List<GetAssetQtyOutputBin> getAssetsQtyByPortfolioIdAndDate(GetTransactionByDateBin bin);

}
