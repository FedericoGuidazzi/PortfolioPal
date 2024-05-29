package com.example.transaction.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.transaction.models.TransactionTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private TransactionTypeEnum type;

    @Column
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
