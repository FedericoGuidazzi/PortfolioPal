package com.example.asset.models.bin;

import com.example.asset.enums.DurationIntervalEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GetAssetBin {
    private String symbol;
    private DurationIntervalEnum duration;
    private LocalDate startDate;
}
