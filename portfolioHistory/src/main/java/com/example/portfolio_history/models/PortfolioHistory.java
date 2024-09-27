package com.example.portfolio_history.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PortfolioHistory {
    private long id;
    private long portfolioId;
    private LocalDate date;
    private BigDecimal investedAmount;
    private BigDecimal countervail;
    private BigDecimal withdrawnAmount;
    private double percentageValue;
}
