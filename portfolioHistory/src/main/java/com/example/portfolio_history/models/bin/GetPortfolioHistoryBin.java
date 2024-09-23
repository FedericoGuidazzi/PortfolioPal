package com.example.portfolio_history.models.bin;

import com.example.portfolio_history.models.enums.DurationIntervalEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPortfolioHistoryBin {
    private long portfolioId;
    private DurationIntervalEnum durationIntervalEnum;
}
