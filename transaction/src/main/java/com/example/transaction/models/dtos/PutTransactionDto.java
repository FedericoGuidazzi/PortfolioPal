package com.example.transaction.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutTransactionDto {

    private String type;

    private LocalDate date;

    private double amount;

    private String symbolId;

    private BigDecimal price;

    private long portfolioId;

    private String currency;

}
