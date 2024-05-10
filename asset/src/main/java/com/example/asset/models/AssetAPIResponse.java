package com.example.asset.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetAPIResponse {

    @JsonProperty("chart")
    private Chart chart;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chart {
        @JsonProperty("result")
        private List<Result> results;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {

        @JsonProperty("meta")
        private Meta meta;

        @JsonProperty("indicators")
        private Indicators indicators;

        @JsonProperty("timestamp")
        private List<Long> timestamps;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        @JsonProperty("currency")
        private String currency;

        @JsonProperty("symbol")
        private String symbol;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Indicators {
        @JsonProperty("quote")
        private List<Quote> quotes;

        @JsonProperty("adjclose")
        private List<AdjClose> adjCloses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Quote {
        @JsonProperty("high")
        private List<Double> highs;

        @JsonProperty("volume")
        private List<Long> volumes;

        @JsonProperty("low")
        private List<Double> lows;

        @JsonProperty("open")
        private List<Double> opens;

        @JsonProperty("close")
        private List<Double> closes;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdjClose {
        @JsonProperty("adjclose")
        private List<Double> adjCloses;
    }
}




