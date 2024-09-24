package com.example.portfolio_history.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private boolean sharePortfolio;
    private String favouriteCurrency;
}
