package com.example.portfolio_history.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portfolio_history.models.entities.PortfolioHistoryEntity;

@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistoryEntity, Long> {

    List<PortfolioHistoryEntity> findByPortfolioIdAndDateAfter(long portfolioId, LocalDate date);

    PortfolioHistoryEntity findByPortfolioIdAndDate(long portfolioId, LocalDate date);

    @Query("SELECT p FROM PortfolioHistoryEntity p ORDER BY p.percentageValue DESC")
    List<PortfolioHistoryEntity> findAllOrderByPercentageValueDesc();

    @Query("SELECT p FROM PortfolioHistoryEntity p WHERE p.portfolioId = :portfolioID ORDER BY p.date DESC")
    Optional<PortfolioHistoryEntity> findTopByPortfolioIdOrderByDateDesc(@Param("portfolioId") long portfolioID);

    @Query("DELETE FROM PortfolioHistoryEntity p WHERE p.portfolioId = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") long portfolioId);

    @Query("DELETE FROM PortfolioHistoryEntity p WHERE p.portfolioId = :portfolioId AND p.date = :date")
    void deleteByPortfolioIdAndDate(@Param("portfolioId") long portfolioId, @Param("date") LocalDate date);
}