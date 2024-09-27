package com.example.portfolio_history.models.bin;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RabbitTransactionBin {
    private LocalDate date;
    private long portfolioId;
}
