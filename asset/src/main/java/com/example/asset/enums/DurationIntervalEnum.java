package com.example.asset.enums;

import lombok.Getter;

@Getter
public enum DurationIntervalEnum {
    S1("1S"),
    A1("1A"),
    A5("5A"),
    MAX("Max");

    private final String value;

    DurationIntervalEnum(String value) {
        this.value = value;
    }

    public static DurationIntervalEnum fromValue(String value) {
        for (DurationIntervalEnum range : DurationIntervalEnum.values()) {
            if (range.getValue().equalsIgnoreCase(value)) {
                return range;
            }
        }
        return DurationIntervalEnum.S1;
    }
}
