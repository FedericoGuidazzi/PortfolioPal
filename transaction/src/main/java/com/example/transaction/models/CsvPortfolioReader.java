package com.example.transaction.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.entities.TransactionEntity;
import com.example.transaction.models.enums.TransactionType;

public class CsvPortfolioReader {

    public static List<TransactionEntity> readCsvFile(InputStream inputStream) throws CustomException {
        List<TransactionEntity> portfolioEntries = new ArrayList<>();

        try (
                Reader reader = new InputStreamReader(inputStream);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("date", "type", "amount", "symbolId", "price", "portfolioId", "currency")
                        .withIgnoreHeaderCase()
                        .withTrim());) {
            for (CSVRecord csvRecord : csvParser) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd").withLocale(Locale.ITALY);
                LocalDate date = LocalDate.parse(csvRecord.get("date"), formatter);
                TransactionEntity entry = TransactionEntity.builder()
                        .type(Optional.ofNullable(
                                TransactionType.fromValue(csvRecord.get("type")))
                                .orElseThrow(() -> new CustomException("Invalid transaction type")))
                        .date(date)
                        .amount(Double.parseDouble(csvRecord.get("amount")))
                        .symbolId(csvRecord.get("symbolId"))
                        .price(BigDecimal.valueOf(Double.parseDouble(csvRecord.get("price"))))
                        .portfolioId(Long.parseLong(csvRecord.get("portfolioId")))
                        .currency(csvRecord.get("currency"))
                        .build();
                portfolioEntries.add(entry);
            }
        } catch (IOException e) {
            throw new CustomException(e);
        }

        return portfolioEntries;
    }
}
