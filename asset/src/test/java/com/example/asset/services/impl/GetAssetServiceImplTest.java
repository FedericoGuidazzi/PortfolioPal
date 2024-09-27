package com.example.asset.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.example.asset.enums.DurationIntervalEnum;
import com.example.asset.models.Asset;
import com.example.asset.models.YahooAPIAssetResponse;
import com.example.asset.models.YahooAPISearch;
import com.example.asset.models.bin.GetAssetBin;
import com.example.asset.utils.RangeUtils;

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

		// Mocking the description response

		// Call the method
		Asset asset = getAssetService.getAsset(assetBin);

		// Assertions
		assertNotNull(asset);
		assertEquals("AAPL", asset.getSymbol());
		assertEquals("USD", asset.getCurrency());
		assertEquals("EQUITY", asset.getAssetClass());
	}

	@Test
	void testGetAssetsMatching() {
		// Configurazione del mock per una risposta valida
		YahooAPISearch mockResponse = Instancio.create(YahooAPISearch.class);
		
		// Chiamata al metodo da testare
		List<String> result = getAssetService.getAssetsMatching(mockResponse.getQuotes().get(0).getSymbol());

		// Verifica del risultato
		assertNotNull(result);

	}
}
