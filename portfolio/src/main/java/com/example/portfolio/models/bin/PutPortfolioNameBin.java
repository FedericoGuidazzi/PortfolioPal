package com.example.portfolio.models.bin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutPortfolioNameBin {
    private String name;
    private long id;
}
