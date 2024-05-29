package com.example.transaction.models;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TransactionTypeEnum {
    BOUGHT("Acquisto"), SOLD("Vendita");

    private final String value;

    private static final Map<String, TransactionTypeEnum> mappedValues = Stream
            .of(TransactionTypeEnum.values())
            .collect(Collectors.toMap(TransactionTypeEnum::getPersistedValue,
                    Function.identity()));

    TransactionTypeEnum(String value) {
        this.value = value;
    }

    public String getPersistedValue() {
        return value;
    }

    public static TransactionTypeEnum fromValue(String value) {
        return mappedValues.get(value);
    }

}
