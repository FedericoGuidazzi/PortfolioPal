package com.example.portfolio_history.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.portfolio_history.models.entities.PortfolioEntity;



public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {

    List<PortfolioEntity> findAllByUserId(String userId);

    @Query("SELECT p.id FROM PortfolioEntity p")
    List<Long> findAllPortfolioIds();

}