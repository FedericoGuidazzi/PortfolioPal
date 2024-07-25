package models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PortfolioHistory {
    private long id;
    private long portfolioID;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal countervail;
    private BigDecimal extraValue;
    private double percentageValue;
}
