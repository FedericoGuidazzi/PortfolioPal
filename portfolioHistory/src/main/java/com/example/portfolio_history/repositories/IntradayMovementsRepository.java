package com.example.portfolio_history.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portfolio_history.models.entities.IntradayMovementsEntity;

@Repository
public interface IntradayMovementsRepository extends JpaRepository<IntradayMovementsEntity, Long> {

    List<IntradayMovementsEntity> findByPortfolioId(long portfolioId);

    // ma che cazzo scrivi conglione, dovrai eliminare la row che ha portfolioId = portfolioId non id = portfolioId..... CAPRA
    // @Query("DELETE FROM IntradayMovementsEntity p WHERE p.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioID") long portfolioId);
}
