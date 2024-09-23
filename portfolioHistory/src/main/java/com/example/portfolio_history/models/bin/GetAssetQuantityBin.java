package com.example.portfolio_history.models.bin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetQuantityBin {
    private String symbolId;

    private double amount;
}
