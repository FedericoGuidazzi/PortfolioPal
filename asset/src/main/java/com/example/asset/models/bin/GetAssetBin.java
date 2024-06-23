package com.example.asset.models.bin;

import com.example.asset.enums.DurationIntervalEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAssetBin {
    private String symbol;
    private DurationIntervalEnum duration;
}
