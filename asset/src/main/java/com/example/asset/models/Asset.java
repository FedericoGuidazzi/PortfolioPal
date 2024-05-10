package com.example.asset.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Asset {
    private String symbol;
    private String currency;
    private List<BigDecimal> prices;
    private List<LocalDate> dates;
}
