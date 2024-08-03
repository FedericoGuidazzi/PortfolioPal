package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private String symbol;
    private String currency;
    private List<BigDecimal> prices;
    private List<LocalDate> dates;
    private String description;
    private String assetClass;
}
