package com.example.user.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private boolean sharePortfolio;
    private String favouriteCurrency;
}
