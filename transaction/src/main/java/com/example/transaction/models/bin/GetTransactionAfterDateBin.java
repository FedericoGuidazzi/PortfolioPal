package com.example.transaction.models.bin;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTransactionAfterDateBin {

    private LocalDate date;

    private long portfolioId;

}
