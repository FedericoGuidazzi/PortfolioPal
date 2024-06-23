package com.example.asset.controllers;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Currency;
import com.example.asset.models.bin.GetCurrencyBin;
import com.example.asset.services.GetCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currency")
public class GetCurrencyController {

    @Autowired
    private GetCurrencyService getCurrencyService;

    @GetMapping("/{currencyFrom}/{currencyTo}")
    public Currency getCurrency(
            @PathVariable String currencyFrom,
            @PathVariable String currencyTo,
            @RequestParam(required = false, defaultValue = "false") boolean mock,
            @RequestParam(required = false, defaultValue = "1S") String duration
    ) {
        if (mock) {
            return mockCurrency(currencyFrom, currencyTo);
        }
        GetCurrencyBin assetBin = GetCurrencyBin.builder()
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .duration(DurationIntervalEnum.fromValue(duration))
                .build();
        return getCurrencyService.getCurrency(assetBin);
    }

    private Currency mockCurrency(String currencyFrom, String currencyTo) {
        return Currency.builder()
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .priceList(List.of(BigDecimal.ONE))
                .dateList(List.of(LocalDate.now()))
                .build();
    }
}
