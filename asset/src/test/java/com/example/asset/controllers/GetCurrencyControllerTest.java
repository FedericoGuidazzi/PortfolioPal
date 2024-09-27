package com.example.asset.controllers;

import com.example.asset.models.Currency;
import com.example.asset.services.GetCurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrencyControllerTest {

    @Mock
    private GetCurrencyService currencyService;

    @InjectMocks
    private GetCurrencyController currencyController;

    @Test
    void test_getAsset() {
        // Mocking the service response
        Currency expectedCurrency = mockCurrency("USD", "EUR");
        when(currencyService.getCurrency(any())).thenReturn(expectedCurrency);

        // Invoking the controller method
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        String duration = "1S";
        boolean mock = false;
        Currency response = currencyController.getCurrency(currencyFrom, currencyTo, mock, duration, null);

        // Assertions
        assertEquals(expectedCurrency, response);
    }

    @Test
    void testGetAssetMocked() {
        // Mocking the service response
        Currency expectedCurrency = Currency.builder()
                .currencyFrom("USD")
                .currencyTo("EUR")
                .priceList(List.of(BigDecimal.ONE))
                .dateList(List.of(LocalDate.now()))
                .build();

        // Invoking the controller method
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        boolean mock = true;
        Currency response = currencyController.getCurrency(currencyFrom, currencyTo, mock, null, null);

        // Assertions
        assertEquals(expectedCurrency, response);
    }

    private Currency mockCurrency(String currencyFrom, String currencyTo) {
        return Currency.builder()
                .dateList(Collections.singletonList(LocalDate.now()))
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .priceList(List.of(BigDecimal.TEN))
                .build();
    }
}