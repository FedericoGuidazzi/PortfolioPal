package com.example.asset.repositories;

import com.example.asset.entities.AssetEntity;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetEntity, String> {
    AssetEntity findBySymbol(String symbol);
}
