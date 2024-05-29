package com.example.transaction.models.bin;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostTransactionBin {

    private long id;
    private String type;
    private LocalDate date;
    private double amount;
    private BigDecimal price;
    private String symbolId;
    private long portfolioId;
    private String currency;

}
