package com.example.asset.controllers;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Asset;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.services.GetAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asset")
public class GetAssetController {

    @Autowired
    GetAssetService getAssetService;

    @GetMapping("/{symbol}")
    public Asset getAsset(
            @PathVariable String symbol,
            @RequestParam(required = false, defaultValue = "false") boolean mock,
            @RequestParam(required = false, defaultValue = "1S") String duration,
            @RequestParam(required = false) LocalDate startDate
    ) {
        if (mock) {
            return mockAsset(symbol);
        }
        GetAssetBin assetBin = GetAssetBin.builder()
                .symbol(symbol)
                .duration(DurationIntervalEnum.fromValue(duration))
                .startDate(startDate)
                .build();
        return getAssetService.getAsset(assetBin);
    }

    @GetMapping("/search/{search}")
    public List<String> getAsset(
            @PathVariable String search
    ) {
        return getAssetService.getAssetsMatching(search);
    }

    private static Asset mockAsset(String symbol) {
        return Asset.builder()
                .dates(Collections.singletonList(LocalDate.now()))
                .currency("EUR")
                .prices(List.of(BigDecimal.ONE))
                .symbol(symbol)
                .build();
    }
}
