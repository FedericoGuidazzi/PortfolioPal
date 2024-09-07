package com.example.portfolio_history.repositories;

import com.example.portfolio_history.models.entities.PortfolioHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistoryEntity, Long> {
    List<PortfolioHistoryEntity> findByPortfolioIDAndDateAfter(long portfolioID, LocalDate date);

    PortfolioHistoryEntity findByPortfolioIDAndDate(long portfolioID, LocalDate date);

    @Query("SELECT p FROM PortfolioHistoryEntity p")
    List<PortfolioHistoryEntity> findTop10ByPercentageValue();

    @Query("SELECT p FROM PortfolioHistoryEntity p WHERE p.portfolioID = :portfolioID ORDER BY p.date DESC")
    Optional<PortfolioHistoryEntity> findTopByPortfolioIDOrderByDateDesc(@Param("portfolioID") long portfolioID);

    @Modifying
    @Transactional
    @Query("DELETE FROM PortfolioHistoryEntity p WHERE p.portfolioID = :portfolioID")
    void deleteByPortfolioID(@Param("portfolioID") long portfolioID);

    @Modifying
    @Transactional
    @Query("DELETE FROM PortfolioHistoryEntity p WHERE p.portfolioID = :portfolioId AND p.date = :date")
    void deleteByPortfolioIdAndDate(@Param("portfolioId") long portfolioId, @Param("date") LocalDate date);
}