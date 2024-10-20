package com.example.asset.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.example.asset.models.Asset;
import com.example.asset.models.YahooAPIAssetResponse;
import com.example.asset.models.YahooAPISearch;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.services.GetAssetService;
import com.example.asset.utils.RangeUtils;

@Service
public class GetAssetServiceImpl implements GetAssetService {

    private final RestTemplate restTemplate = new RestTemplate();

    String description;

    @Override
    public Asset getAsset(GetAssetBin assetBin) {

        // call Yahoo Finance API to get data regarding the asset
        int range = RangeUtils.rangeMap.getOrDefault(assetBin.getDuration().getValue(), 7);
        LocalDate startDate = Optional.ofNullable(assetBin.getStartDate()).orElse(LocalDate.now().minusDays(range));

        String url = "https://query2.finance.yahoo.com/v8/finance/chart/" +
                assetBin.getSymbol() +
                "?formatted=true&crumb=mjbGZAqKo3g&lang=it-IT&region=IT&includeAdjustedClose=true&interval=1d&period1="
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

            // Mt(15px) Lh(1.6) -> asset
            // prof-desc -> crypto
            try {
                String urlDescription = "https://it.finance.yahoo.com/quote/" + assetBin.getSymbol() + "/profile";
                ResponseEntity<String> responseDescription = restTemplate.getForEntity(urlDescription, String.class);

                String responseDescriptionBody = Optional.ofNullable(responseDescription.getBody()).orElse("");
                // Pattern per trovare il contenuto dopo il tag specificato
                String tag = "Mt(15px) Lh(1.6)";

                int index = responseDescriptionBody.indexOf(tag);
                if (index != -1) {
                    description = getDescription(responseDescriptionBody, index);
                } else {
                    tag = "prof-desc";
                    index = responseDescriptionBody.indexOf(tag);

                    if (index != -1) {
                        description = getDescription(responseDescriptionBody, index);
                    }
                }

            } catch (Exception e) {
                description = "Descrizione non trovata";
            }

            // Trovare e stampare il contenuto

            List<BigDecimal> prices = Optional.ofNullable(quotes).filter(el -> !CollectionUtils.isEmpty(el))
                    .map(el -> el.get(0))
                    .map(e -> Optional.ofNullable(e.getCloses()).orElse(Collections.emptyList()).stream()
                            .map(x -> {
                                if (Objects.isNull(x)) {
                                    return Double.valueOf(1);
                                }
                                return x;
                            }).map(BigDecimal::valueOf).toList())
                    .orElse(List.of());
            double percentage = prices.isEmpty() ? 0
                    : (prices.get(prices.size() - 1).doubleValue() - prices.get(0).doubleValue())
                            / prices.get(0).doubleValue() * 100;

            return Asset.builder()
                    .dates(Optional.ofNullable(result)
                            .map(el -> el.getTimestamps().stream().map(this::localDateFromTimestamp).toList())
                            .orElse(null))
                    .prices(prices)
                    .currency(Optional.ofNullable(result).map(YahooAPIAssetResponse.Result::getMeta)
                            .map(YahooAPIAssetResponse.Meta::getCurrency).orElse(null))
                    .symbol(Optional.ofNullable(result).map(YahooAPIAssetResponse.Result::getMeta)
                            .map(YahooAPIAssetResponse.Meta::getSymbol).orElse(null))
                    .description(description)
                    .assetClass(getAssetClass(assetBin.getSymbol()))
                    .percentage(percentage)
                    .build();

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance: " + e.getMessage());
            return Asset.builder().build();
        }
    }

    private String getAssetClass(String search) {
        String url = "https://query1.finance.yahoo.com/v1/finance/search?q=" +
                search
                + "&lang=it-IT&region=IT&quotesCount=6&newsCount=4&enableFuzzyQuery=false&quotesQueryId=tss_match_phrase_query&multiQuoteQueryId=multi_quote_single_token_query&enableCb=true&enableNavLinks=true&enableEnhancedTrivialQuery=true&enableCulturalAssets=true&enableLogoUrl=true";

        AtomicReference<String> assetClass = new AtomicReference<>("");
        try {
            ResponseEntity<YahooAPISearch> response = restTemplate.getForEntity(url, YahooAPISearch.class);
            Optional.ofNullable(response.getBody())
                    .map(YahooAPISearch::getQuotes)
                    .ifPresent(e -> e.forEach(el -> {
                        if (search.equals(el.getSymbol())) {
                            assetClass.set(el.getQuoteType());
                        }
                    }));

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance Asset search");
        }
        if (assetClass.get().isBlank()) {
            assetClass.set("OTHERS");
        }
        return assetClass.get();
    }

    @Override
    public List<String> getAssetsMatching(String search) {
        String url = "https://query1.finance.yahoo.com/v1/finance/search?q=" +
                search
                + "&lang=it-IT&region=IT&quotesCount=6&newsCount=4&enableFuzzyQuery=false&quotesQueryId=tss_match_phrase_query&multiQuoteQueryId=multi_quote_single_token_query&enableCb=true&enableNavLinks=true&enableEnhancedTrivialQuery=true&enableCulturalAssets=true&enableLogoUrl=true";

        List<String> stringList = new ArrayList<>();
        try {
            ResponseEntity<YahooAPISearch> response = restTemplate.getForEntity(url, YahooAPISearch.class);
            Optional.ofNullable(response.getBody())
                    .map(YahooAPISearch::getQuotes)
                    .ifPresent(e -> e.forEach(el -> stringList.add(el.getSymbol())));
            stringList.removeAll(Collections.singleton(null));

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance Asset search");
        }
        return stringList;
    }

    private static String getDescription(String responseDescriptionBody, int index) {
        String regex = ">([^<]*)<";

        // Compilare il pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseDescriptionBody.substring(index));

        String x = "";
        // Trovare e stampare il contenuto
        if (matcher.find()) {
            x = matcher.group(1);
        }
        return x;
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
