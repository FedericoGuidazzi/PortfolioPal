package com.example.asset.services.impl;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Asset;
import com.example.asset.models.YahooAPIAssetResponse;
import com.example.asset.models.YahooAPISearch;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.utils.RangeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAssetServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GetAssetServiceImpl getAssetService;

    @Test
    void testGetAsset() {
        // Mocking RangeUtils
        RangeUtils.rangeMap.put("short", 7);

        // Create a mock GetAssetBin
        GetAssetBin assetBin = GetAssetBin.builder().symbol("AAPL").duration(DurationIntervalEnum.S1).build();

        // Create a mock response for YahooAPIAssetResponse
        YahooAPIAssetResponse.Quote quote = new YahooAPIAssetResponse.Quote();
        quote.setCloses(List.of(150.0, 152.0, 148.0));
        YahooAPIAssetResponse.Indicators indicators = new YahooAPIAssetResponse.Indicators();
        indicators.setQuotes(List.of(quote));
        YahooAPIAssetResponse.Meta meta = new YahooAPIAssetResponse.Meta();
        meta.setCurrency("USD");
        meta.setSymbol("AAPL");
        YahooAPIAssetResponse.Result result = new YahooAPIAssetResponse.Result();
        result.setTimestamps(List.of(1609459200L, 1609545600L, 1609632000L));
        result.setIndicators(indicators);
        result.setMeta(meta);
        YahooAPIAssetResponse.Chart chart = new YahooAPIAssetResponse.Chart();
        chart.setResults(List.of(result));
        YahooAPIAssetResponse response = new YahooAPIAssetResponse();
        response.setChart(chart);

        YahooAPISearch yahooAPISearchResponse = YahooAPISearch.builder().quotes(List.of(YahooAPISearch.Quote.builder().symbol("AAPL").quoteType("ASSET").build())).build();

        when(restTemplate.getForEntity(anyString(), eq(YahooAPIAssetResponse.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        when(restTemplate.getForEntity(anyString(), eq(YahooAPISearch.class)))
                .thenReturn(new ResponseEntity<>(yahooAPISearchResponse, HttpStatus.OK));

        // Mocking the description response
        String descriptionBody = "<html><body><div class=\"Mt(15px) Lh(1.6)\">Apple Inc. is a...</div></body></html>";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(descriptionBody, HttpStatus.OK));

        // Call the method
        Asset asset = getAssetService.getAsset(assetBin);

        // Assertions
        assertNotNull(asset);
        assertEquals("AAPL", asset.getSymbol());
        assertEquals("USD", asset.getCurrency());
        assertEquals("ASSET", asset.getAssetClass());
        assertEquals("Apple Inc. is a...", asset.getDescription());
        assertEquals(List.of(BigDecimal.valueOf(150.0), BigDecimal.valueOf(152.0), BigDecimal.valueOf(148.0)), asset.getPrices());
        assertEquals(List.of(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 2), LocalDate.of(2021, 1, 3)), asset.getDates());
    }
}
