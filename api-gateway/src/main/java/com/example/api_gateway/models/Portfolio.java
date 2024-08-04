package com.example.api_gateway.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Portfolio {

    private long id;
    private String name;
    private String userId;
    private boolean sharePortfolio;
}
