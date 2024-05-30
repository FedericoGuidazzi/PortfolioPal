package com.example.transaction.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    private long id;

    private TransactionTypeEnum type;

    private LocalDate date;

    private double amount;

    private String symbolId;

    private BigDecimal price;

    private long portfolioId;

    private String currency;

}
