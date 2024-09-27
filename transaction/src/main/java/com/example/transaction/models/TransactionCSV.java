package com.example.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCSV {
    private String date;

    private String type;

    private double amount;

    private String symbolId;

    private double price;

    private String currency;

}
