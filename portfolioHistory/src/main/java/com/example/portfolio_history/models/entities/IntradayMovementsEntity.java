package com.example.portfolio_history.models.entities;

import com.example.portfolio_history.models.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "intraday_movement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntradayMovementsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type", nullable = false)
    private TransactionTypeEnum type;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "symbol_id", nullable = false)
    private String symbolId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "portfolio_id", nullable = false)
    private long portfolioId;

    @Column(name = "currency", nullable = false)
    private String currency;
}
