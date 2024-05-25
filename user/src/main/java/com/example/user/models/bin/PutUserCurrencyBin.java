package com.example.user.models.bin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutUserCurrencyBin {
    private String userID;
    private String currency;
}
