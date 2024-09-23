package com.example.portfolio_history.models.bin;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovementBin {
    private String type;
    private LocalDate date;
    private double amount;
    private String symbolId;
    private BigDecimal price;
    private long portfolioId;
    private String currency;
}
