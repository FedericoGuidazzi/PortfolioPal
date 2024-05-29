package com.example.transaction.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.transaction.models.entities.TransactionEntity;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

}
