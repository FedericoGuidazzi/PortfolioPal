package com.example.asset.services.impl;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Asset;
import com.example.asset.models.AssetAPIResponse;
import com.example.asset.models.bin.GetAssetBin;
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

    @Test
    void test_getAsset() {
        // Mocking the response from the external API
        AssetAPIResponse apiResponse = createMockAssetAPIResponse();
        when(restTemplate.getForEntity(any(String.class), any())).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

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

    private AssetAPIResponse createMockAssetAPIResponse() {
        AssetAPIResponse.Quote quote = new AssetAPIResponse.Quote();
        quote.setCloses(List.of(123.45));

        AssetAPIResponse.Meta meta = new AssetAPIResponse.Meta();
        meta.setCurrency("USD");
        meta.setSymbol("AAPL");

        AssetAPIResponse.Result result = new AssetAPIResponse.Result();
        result.setTimestamps(Collections.singletonList(1671993600L));
        result.setIndicators(new AssetAPIResponse.Indicators());
        result.getIndicators().setQuotes(List.of(quote));
        result.setMeta(meta);

        AssetAPIResponse.Chart chart = AssetAPIResponse.Chart.builder().build();
        chart.setResults(Collections.singletonList(result));

        return new AssetAPIResponse(chart);
    }
}
