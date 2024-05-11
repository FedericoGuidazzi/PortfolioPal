package com.example.asset.entities;

import com.example.asset.enums.AssetClass;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class AssetEntity {

    @Id
    String symbol;
    String description;
    AssetClass assetClass;
}
