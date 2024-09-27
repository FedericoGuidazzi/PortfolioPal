package com.example.transaction.models.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TransactionType {
    BUY("Acquisto"), SELL("Vendita");

    private final String value;

    private static final Map<String, TransactionType> mappedValues = Stream
            .of(TransactionType.values())
            .collect(Collectors.toMap(TransactionType::getPersistedValue,
                    Function.identity()));

    TransactionType(String value) {
        this.value = value;
    }

    public String getPersistedValue() {
        return value;
    }

    public static TransactionType fromValue(String value) {
        return mappedValues.get(value);
    }

}
