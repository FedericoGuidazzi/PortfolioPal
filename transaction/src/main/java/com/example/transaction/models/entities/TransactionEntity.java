package com.example.transaction.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.transaction.models.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private TransactionType type;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @Column
    private double amount;

    @Column
    private String symbolId;

    @Column
    private BigDecimal price;

    @Column
    private long portfolioId;

    @Column
    private String currency;

}
