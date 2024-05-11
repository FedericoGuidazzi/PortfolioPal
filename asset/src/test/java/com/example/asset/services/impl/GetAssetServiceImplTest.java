package com.example.asset.services.impl;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Asset;
import com.example.asset.models.YahooAPIResponse;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.repositories.AssetRepository;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAssetServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GetAssetServiceImpl service;

    @Mock
    private AssetRepository assetRepository;

    @Test
    void test_getAsset() {
        // Mocking the response from the external API
        YahooAPIResponse apiResponse = createMockAssetAPIResponse();
        when(restTemplate.getForEntity(any(String.class), any())).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));
        when(assetRepository.save(any())).thenReturn(null);
        // Invoking the method under test
        GetAssetBin bin = GetAssetBin.builder().build();
        bin.setSymbol("AAPL");
        bin.setDuration(DurationIntervalEnum.S1);
        Asset result = service.getAsset(bin);

        // Assertions
        assertEquals("AAPL", result.getSymbol());
        assertEquals("USD", result.getCurrency());
        assertEquals(1, result.getDates().size());
        assertEquals(LocalDate.of(2022, 12, 25), result.getDates().get(0));
        assertEquals(1, result.getPrices().size());
        assertEquals(BigDecimal.valueOf(123.45), result.getPrices().get(0));
    }

    private YahooAPIResponse createMockAssetAPIResponse() {
        YahooAPIResponse.Quote quote = new YahooAPIResponse.Quote();
        quote.setCloses(List.of(123.45));

        YahooAPIResponse.Meta meta = new YahooAPIResponse.Meta();
        meta.setCurrency("USD");
        meta.setSymbol("AAPL");

        YahooAPIResponse.Result result = new YahooAPIResponse.Result();
        result.setTimestamps(Collections.singletonList(1671993600L));
        result.setIndicators(new YahooAPIResponse.Indicators());
        result.getIndicators().setQuotes(List.of(quote));
        result.setMeta(meta);

        YahooAPIResponse.Chart chart = YahooAPIResponse.Chart.builder().build();
        chart.setResults(Collections.singletonList(result));

        return new YahooAPIResponse(chart);
    }
}
