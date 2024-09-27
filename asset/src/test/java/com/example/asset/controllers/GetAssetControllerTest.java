package com.example.asset.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.asset.models.Asset;
import com.example.asset.services.GetAssetService;

@ExtendWith(MockitoExtension.class)
public class GetAssetControllerTest {

    @Mock
    private GetAssetService assetService;

    @InjectMocks
    private GetAssetController assetController;

    @Test
    void test_getAsset() {
        // Mocking the service response
        Asset expectedAsset = mockAsset("AAPL");
        when(assetService.getAsset(any())).thenReturn(expectedAsset);

        // Invoking the controller method
        String symbol = "AAPL";
        String duration = "1S";
        boolean mock = false;
        Asset response = assetController.getAsset(symbol, mock, duration, null);

        // Assertions
        assertEquals(expectedAsset, response);
    }

    @Test
    void testGetAssetMocked() {
        // Configurazione del mock
        Asset expectedAsset = Asset.builder()
                .dates(Collections.singletonList(LocalDate.now()))
                .currency("EUR")
                .prices(List.of(BigDecimal.ONE))
                .symbol("AAPL")
                .build();

        // Invoking the controller method
        String symbol = "AAPL";

        boolean mock = true;
        Asset response = assetController.getAsset(symbol, mock, null, null);

        // Assertions
        assertEquals(expectedAsset, response);
    }

    @Test
    void test_getAssetWithStartDate() {
        // Mocking the service response
        Asset expectedAsset = mockAsset("AAPL");
        when(assetService.getAsset(any())).thenReturn(expectedAsset);

        // Invoking the controller method
        String symbol = "AAPL";
        String duration = "1S";
        boolean mock = false;
        Asset response = assetController.getAsset(symbol, mock, duration, LocalDate.now());

        // Assertions
        assertEquals(expectedAsset, response);
    }

    private Asset mockAsset(String symbol) {
        return Asset.builder()
                .dates(Collections.singletonList(LocalDate.now()))
                .currency("EUR")
                .prices(List.of(BigDecimal.TEN))
                .symbol(symbol)
                .build();
    }
}
