package com.example.portfolio_history.models;

import com.example.portfolio_history.models.enums.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovementBin {
    private TransactionTypeEnum type;
    private LocalDate date;
    private double amount;
    private String symbolId;
    private BigDecimal price;
    private long portfolioId;
    private String currency;
}
