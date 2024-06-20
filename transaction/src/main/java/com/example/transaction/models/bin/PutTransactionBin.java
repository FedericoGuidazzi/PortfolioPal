package com.example.transaction.models.bin;

import com.example.transaction.models.dtos.PutTransactionDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutTransactionBin {

    private long id;
    private PutTransactionDto transaction;

}
