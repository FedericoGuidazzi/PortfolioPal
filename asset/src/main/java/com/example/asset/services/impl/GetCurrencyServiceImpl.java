package com.example.asset.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.example.asset.models.Currency;
import com.example.asset.models.YahooAPIAssetResponse;
import com.example.asset.models.bin.GetCurrencyBin;
import com.example.asset.services.GetCurrencyService;
import com.example.asset.utils.RangeUtils;

@Service
public class GetCurrencyServiceImpl implements GetCurrencyService {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Currency getCurrency(GetCurrencyBin currencyBin) {
        // call Yahoo Finance API to get data regarding the asset
        int range = RangeUtils.rangeMap.getOrDefault(currencyBin.getDuration().getValue(), 7);
        LocalDate startDate = Optional.ofNullable(currencyBin.getStartDate()).orElse(LocalDate.now().minusDays(range));

        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" +
                currencyBin.getCurrencyFrom() +
                currencyBin.getCurrencyTo() +
                "=X?formatted=true&crumb=mjbGZAqKo3g&lang=it-IT&region=IT&includeAdjustedClose=true&interval=1d&period1="
                +
                timestampFromLocalDate(startDate) +
                "&period2=" +
                timestampFromLocalDate(LocalDate.now()) +
                "&events=capitalGain%7Cdiv%7Csplit&useYfid=true&corsDomain=it.finance.yahoo.com";
        try {
            ResponseEntity<YahooAPIAssetResponse> response = restTemplate.getForEntity(url,
                    YahooAPIAssetResponse.class);
            YahooAPIAssetResponse.Result result = Optional.ofNullable(response.getBody())
                    .map(YahooAPIAssetResponse::getChart)
                    .map(YahooAPIAssetResponse.Chart::getResults)
                    .filter(el -> !CollectionUtils.isEmpty(el))
                    .map(el -> el.get(0))
                    .orElse(null);

            List<YahooAPIAssetResponse.Quote> quotes = Optional.ofNullable(result)
                    .map(YahooAPIAssetResponse.Result::getIndicators)
                    .map(YahooAPIAssetResponse.Indicators::getQuotes).orElse(null);

            return Currency.builder()
                    .dateList(Optional.ofNullable(result)
                            .map(el -> el.getTimestamps().stream().map(this::localDateFromTimestamp).toList())
                            .orElse(null))
                    .priceList(Optional.ofNullable(quotes).filter(el -> !CollectionUtils.isEmpty(el))
                            .map(el -> el.get(0)).map(e -> Optional.ofNullable(e.getCloses())
                                    .orElse(Collections.emptyList()).stream().map(x -> {
                                        if (Objects.isNull(x)) {
                                            return Double.valueOf(1);
                                        }
                                        return x;
                                    }).map(BigDecimal::valueOf).toList())
                            .orElse(null))
                    .currencyFrom(currencyBin.getCurrencyFrom())
                    .currencyTo(currencyBin.getCurrencyTo())
                    .build();

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance");
            return Currency.builder().build();
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
