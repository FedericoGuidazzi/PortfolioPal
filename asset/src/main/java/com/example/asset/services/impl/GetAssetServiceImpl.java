package com.example.asset.services.impl;

import com.example.asset.models.Asset;
import com.example.asset.models.YahooAPIAssetResponse;
import com.example.asset.models.YahooAPISearch;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.services.GetAssetService;
import com.example.asset.utils.RangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GetAssetServiceImpl implements GetAssetService {

    @Autowired
    private RestTemplate restTemplate;

    String description;


    @Override
    public Asset getAsset(GetAssetBin assetBin) {

        //call Yahoo Finance API to get data regarding the asset
        LocalDate startDate;
        if (assetBin.getStartDate() == null) {
            int range = RangeUtils.rangeMap.getOrDefault(assetBin.getDuration().getValue(), 7);
            startDate = LocalDate.now().minusDays(range);
        } else {
            startDate = assetBin.getStartDate();
        }


        String url = "https://query2.finance.yahoo.com/v8/finance/chart/" +
                assetBin.getSymbol() +
                "?formatted=true&crumb=mjbGZAqKo3g&lang=it-IT&region=IT&includeAdjustedClose=true&interval=1d&period1=" +
                timestampFromLocalDate(startDate) +
                "&period2=" +
                timestampFromLocalDate(LocalDate.now()) +
                "&events=capitalGain%7Cdiv%7Csplit&useYfid=true&corsDomain=it.finance.yahoo.com";

        try {
            ResponseEntity<YahooAPIAssetResponse> response = restTemplate.getForEntity(url, YahooAPIAssetResponse.class);
            YahooAPIAssetResponse.Result result = Optional.ofNullable(response.getBody())
                    .map(YahooAPIAssetResponse::getChart)
                    .map(YahooAPIAssetResponse.Chart::getResults)
                    .filter(el -> !CollectionUtils.isEmpty(el))
                    .map(el -> el.get(0))
                    .orElse(null);

            List<YahooAPIAssetResponse.Quote> quotes = Optional.ofNullable(result).map(YahooAPIAssetResponse.Result::getIndicators)
                    .map(YahooAPIAssetResponse.Indicators::getQuotes).orElse(null);


            //Mt(15px) Lh(1.6) -> asset
            //prof-desc -> crypto
            String urlDescription = "https://it.finance.yahoo.com/quote/" + assetBin.getSymbol() + "/profile";
            ResponseEntity<String> responseDescription = restTemplate.getForEntity(urlDescription, String.class);

            String responseDescriptionBody = Optional.ofNullable(responseDescription.getBody()).orElse("");
            // Pattern per trovare il contenuto dopo il tag specificato
            String tag = "Mt(15px) Lh(1.6)";

            int index = responseDescriptionBody.indexOf(tag);

            // Trovare e stampare il contenuto
            if (index != -1) {
                description = getDescription(responseDescriptionBody, index);
            } else {
                tag = "prof-desc";
                index = responseDescriptionBody.indexOf(tag);

                if (index != -1) {
                    description = getDescription(responseDescriptionBody, index);
                }
            }
            return Asset.builder()
                    .dates(Optional.ofNullable(result).map(el -> el.getTimestamps().stream().map(this::localDateFromTimestamp).toList()).orElse(null))
                    .prices(Optional.ofNullable(quotes).filter(el -> !CollectionUtils.isEmpty(el)).map(el -> el.get(0)).map(e -> Optional.ofNullable(e.getCloses()).orElse(Collections.emptyList()).stream().map(x -> {
                        if (Objects.isNull(x)) {
                            return Double.valueOf(1);
                        }
                        return x;
                    }).map(BigDecimal::valueOf).toList()).orElse(null))
                    .currency(Optional.ofNullable(result).map(YahooAPIAssetResponse.Result::getMeta).map(YahooAPIAssetResponse.Meta::getCurrency).orElse(null))
                    .symbol(Optional.ofNullable(result).map(YahooAPIAssetResponse.Result::getMeta).map(YahooAPIAssetResponse.Meta::getSymbol).orElse(null))
                    .description(description)
                    .assetClass(getAssetClass(assetBin.getSymbol()))
                    .build();

        } catch (Exception e) {
            System.err.println("Exception while calling Yahoo Finance");
            return Asset.builder().build();
        }
    }

    private String getAssetClass(String search) {
        String url = "https://query1.finance.yahoo.com/v1/finance/search?q=" +
                search + "&lang=it-IT&region=IT&quotesCount=6&newsCount=4&enableFuzzyQuery=false&quotesQueryId=tss_match_phrase_query&multiQuoteQueryId=multi_quote_single_token_query&enableCb=true&enableNavLinks=true&enableEnhancedTrivialQuery=true&enableCulturalAssets=true&enableLogoUrl=true";

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
                search + "&lang=it-IT&region=IT&quotesCount=6&newsCount=4&enableFuzzyQuery=false&quotesQueryId=tss_match_phrase_query&multiQuoteQueryId=multi_quote_single_token_query&enableCb=true&enableNavLinks=true&enableEnhancedTrivialQuery=true&enableCulturalAssets=true&enableLogoUrl=true";

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
