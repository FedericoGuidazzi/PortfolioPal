package com.example.asset.services.impl;

import com.example.asset.models.Asset;
import com.example.asset.models.AssetAPIResponse;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.services.GetAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
public class GetAssetServiceImpl implements GetAssetService {

    @Autowired
    private RestTemplate restTemplate;

    private final Map<String, Integer> rangeMap = new HashMap<String, Integer>() {{
        put("1S", 7);
        put("1A", 365);
        put("5A", 1825);
        put("Max", 36135);
    }};


    @Override
    public Asset getAsset(GetAssetBin assetBin) {

        //call Yahoo Finance API to get data regarding the asset
        int range = rangeMap.getOrDefault(assetBin.getDuration().getValue(), 7);
        LocalDate startDate = LocalDate.now().minusDays(range);

        String url = "https://query2.finance.yahoo.com/v8/finance/chart/" +
                assetBin.getSymbol() +
                "?formatted=true&crumb=mjbGZAqKo3g&lang=it-IT&region=IT&includeAdjustedClose=true&interval=1d&period1=" +
                timestampFromLocalDate(startDate) +
                "&period2=" +
                timestampFromLocalDate(LocalDate.now()) +
                "&events=capitalGain%7Cdiv%7Csplit&useYfid=true&corsDomain=it.finance.yahoo.com";
        try {
            ResponseEntity<AssetAPIResponse> response = restTemplate.getForEntity(url, AssetAPIResponse.class);
            AssetAPIResponse.Result result = Optional.ofNullable(response.getBody())
                    .map(AssetAPIResponse::getChart)
                    .map(AssetAPIResponse.Chart::getResults)
                    .filter(el -> !CollectionUtils.isEmpty(el))
                    .map(el -> el.get(0))
                    .orElse(null);

            List<AssetAPIResponse.Quote> quotes = Optional.ofNullable(result).map(AssetAPIResponse.Result::getIndicators)
                    .map(AssetAPIResponse.Indicators::getQuotes).orElse(null);

            return Asset.builder()
                    .dates(Optional.ofNullable(result).map(el -> el.getTimestamps().stream().map(this::localDateFromTimestamp).toList()).orElse(null))
                    .prices(Optional.ofNullable(quotes).filter(el -> !CollectionUtils.isEmpty(el)).map(el -> el.get(0)).map(e -> Optional.ofNullable(e.getCloses()).orElse(Collections.emptyList()).stream().map(BigDecimal::valueOf).toList()).orElse(null))
                    .currency(Optional.ofNullable(result).map(AssetAPIResponse.Result::getMeta).map(AssetAPIResponse.Meta::getCurrency).orElse(null))
                    .symbol(Optional.ofNullable(result).map(AssetAPIResponse.Result::getMeta).map(AssetAPIResponse.Meta::getSymbol).orElse(null))
                    .build();

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance");
            return Asset.builder().build();
        }
    }

    public LocalDate localDateFromTimestamp(Long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public long timestampFromLocalDate(LocalDate date) {
        LocalDateTime localDateTime = date.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toEpochSecond();
    }
}
