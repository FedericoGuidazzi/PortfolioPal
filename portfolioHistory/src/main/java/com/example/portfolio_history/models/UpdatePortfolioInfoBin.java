package com.example.portfolio_history.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePortfolioInfoBin {
    private Long id;
    private boolean isSharable;
}
