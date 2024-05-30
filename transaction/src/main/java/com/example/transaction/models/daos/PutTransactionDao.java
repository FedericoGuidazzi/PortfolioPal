package com.example.transaction.models.daos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutTransactionDao {
    private String type;
    private LocalDate date;
    private double amount;
    private BigDecimal price;
    private String symbolId;
    private long portfolioId;
    private String currency;
}
