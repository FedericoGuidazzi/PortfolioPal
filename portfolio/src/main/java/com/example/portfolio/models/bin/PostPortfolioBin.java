package com.example.portfolio.models.bin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostPortfolioBin {
    private String name;
    private String userId;
}
