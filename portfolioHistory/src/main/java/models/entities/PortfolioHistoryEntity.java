package models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_history")
@Data
public class PortfolioHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "portfolio_id", nullable = false)
    private long portfolioID;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "countervail", nullable = false)
    private BigDecimal countervail;

    @Column(name = "extra_value", nullable = false)
    private BigDecimal extra_value;

    @Column(name = "percentage_value", nullable = false)
    private double percentageValue;

}
