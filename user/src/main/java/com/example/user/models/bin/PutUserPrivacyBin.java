package com.example.user.models.bin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutUserPrivacyBin {
    private String userID;
    private boolean isSherable;
}
