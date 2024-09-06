package com.example.portfolio.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.portfolio.models.entities.PortfolioEntity;

public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {

    public List<PortfolioEntity> findAllByUserId(String userId);

}