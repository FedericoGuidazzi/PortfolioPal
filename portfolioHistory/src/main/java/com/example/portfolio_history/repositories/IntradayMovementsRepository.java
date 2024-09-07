package com.example.portfolio_history.repositories;

import com.example.portfolio_history.models.entities.IntradayMovementsEntity;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntradayMovementsRepository extends JpaRepository<IntradayMovementsEntity, Long> {

    List<IntradayMovementsEntity> findByPortfolioId(long portfolioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM IntradayMovementsEntity p WHERE p.id = :portfolioID")
    void deleteByPortfolioId(@Param("portfolioID") long portfolioID);
}
