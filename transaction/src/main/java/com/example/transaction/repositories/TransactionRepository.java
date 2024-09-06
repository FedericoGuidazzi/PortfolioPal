package com.example.transaction.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.transaction.models.bin.GetAssetQtyOutputBin;
import com.example.transaction.models.entities.TransactionEntity;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByPortfolioId(long portfolioId);

    @Query("select t from TransactionEntity t where t.date >= :date and t.date < CURRENT_DATE and t.portfolioId = :portfolioId")
    List<TransactionEntity> findAllByPortfolioIdAndDateAfter(@Param("portfolioId") long portfolioId,
            @Param("date") LocalDate date);

    @Query("SELECT new com.example.transaction.models.bin.GetAssetQtyOutputBin(t.symbolId, SUM(CASE WHEN t.type = com.example.transaction.models.enums.TransactionType.BUY THEN t.amount ELSE -t.amount END)) FROM TransactionEntity t WHERE t.portfolioId = :portfolioId and t.date < :date GROUP BY t.symbolId")
    List<GetAssetQtyOutputBin> findAssetsQtyByPortfolioIdAndDate(@Param("portfolioId") long portfolioId,
            @Param("date") LocalDate date);
}
