package com.example.asset.services;

import com.example.asset.models.Currency;
import com.example.asset.models.bin.GetCurrencyBin;

public interface GetCurrencyService {
    Currency getCurrency(GetCurrencyBin currencyBin);
}
