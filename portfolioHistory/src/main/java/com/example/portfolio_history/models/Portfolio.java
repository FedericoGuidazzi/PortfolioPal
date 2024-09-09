package com.example.portfolio_history.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Portfolio {

    private long id;
    private String name;
    private String userId;
    private boolean isSherable;
}
