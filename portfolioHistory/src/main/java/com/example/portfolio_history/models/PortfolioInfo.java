package com.example.portfolio_history.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioInfo {

    private String portfolioName;
    private Long idPortfolio;
    private double percentageValue;
}
