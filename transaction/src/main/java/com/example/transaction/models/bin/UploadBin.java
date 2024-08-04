package com.example.transaction.models.bin;

import java.io.InputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadBin {

    long portfolioId;

    InputStream inputStream;

}
