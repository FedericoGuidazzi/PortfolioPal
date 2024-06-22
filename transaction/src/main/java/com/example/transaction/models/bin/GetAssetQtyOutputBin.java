package com.example.transaction.models.bin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetQtyOutputBin {

    private String symbolId;

    private double amount;

}
