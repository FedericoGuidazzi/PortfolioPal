package models;

import lombok.Builder;
import lombok.Data;
import models.enums.DurationIntervalEnum;

@Data
@Builder
public class GetPortfolioHistoryBin {
    private long portfolioId;
    private DurationIntervalEnum durationIntervalEnum;
}
