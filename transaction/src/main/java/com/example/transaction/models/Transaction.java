package com.example.transaction.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    private long id;

    private LocalDate date;

    private BigDecimal price;

    private double quantity;

    private String symbolId;

    private long portfolioId;

    private String currency;

}
