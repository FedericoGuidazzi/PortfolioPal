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
public class Currency {
    private String currencyFrom;
    private String currencyTo;
    private List<BigDecimal> priceList;
    private List<LocalDate> dateList;
}