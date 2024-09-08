package com.example.portfolio_history.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portfolio_history.models.entities.PortfolioPrivacyInfoEntity;

@Repository
public interface PortfolioInfoRepository extends JpaRepository<PortfolioPrivacyInfoEntity, Long> {
    @Query("SELECT p.portfolioId FROM PortfolioPrivacyInfoEntity p")
    List<Long> findAllPortfolioIds();

    @Query("UPDATE PortfolioPrivacyInfoEntity r SET r.isSharable = :isSharable WHERE r.portfolioId = :portfolioId")
    void updateSharabilityById(@Param("portfolioId") Long portfolioId, @Param("isSharable") boolean isSharable);

    @Query("DELETE FROM PortfolioPrivacyInfoEntity p WHERE p.portfolioID = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") long portfolioId);
}
