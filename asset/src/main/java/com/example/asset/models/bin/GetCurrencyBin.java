package com.example.asset.models.bin;

import com.example.asset.enums.DurationIntervalEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetCurrencyBin {
    private String currencyFrom;
    private String currencyTo;
    private DurationIntervalEnum duration;
}
