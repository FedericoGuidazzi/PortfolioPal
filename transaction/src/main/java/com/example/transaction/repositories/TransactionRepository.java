package com.example.transaction.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.transaction.models.entities.TransactionEntity;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByPortfolioId(long portfolioId);
}
