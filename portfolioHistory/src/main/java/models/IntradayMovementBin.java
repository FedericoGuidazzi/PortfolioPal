package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.enums.TransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntradayMovementBin {
    private TransactionTypeEnum type;
    private LocalDate date;
    private double amount;
    private String symbolId;
    private BigDecimal price;
    private long portfolioId;
    private String currency;
}
