package com.example.transaction;

public enum AuthToBoolean {
    
    TRUE("true"),
    FALSE("false");

    private final String value;

    AuthToBoolean(String value) {
        this.value = value;
    }

    private String getValue() {
        return value;
    }

    public static boolean getBoolFromValue(String value) {
        for (AuthToBoolean authToBoolean : AuthToBoolean.values()) {
            if (authToBoolean.getValue().toLowerCase().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
