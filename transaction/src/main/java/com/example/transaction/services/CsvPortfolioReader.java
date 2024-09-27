package com.example.transaction.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.TransactionCSV;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

public class CsvPortfolioReader {

    public static List<TransactionCSV> readCsvFile(InputStream inputStream) throws CustomException {

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            return new CsvToBeanBuilder<TransactionCSV>(csvReader)
                    .withType(TransactionCSV.class)
                    .build()
                    .parse();

        } catch (IOException e) {
            throw new CustomException("Errore durante la lettura del file CSV: " + e.getMessage());
        }

    }
}
