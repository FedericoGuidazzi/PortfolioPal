package com.example.asset.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Currency {
    private String currencyFrom;
    private String currencyTo;
    private List<BigDecimal> priceList;
    private List<LocalDate> dateList;
}
