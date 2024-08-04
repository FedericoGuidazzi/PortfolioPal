package com.example.transaction.models.enums;

public enum AuthBoolean {
    
    TRUE("true"),
    FALSE("false");

    private final String value;

    AuthBoolean(String value) {
        this.value = value;
    }

    private String getValue() {
        return value;
    }

    public static boolean fromValue(String value) {
        for (AuthBoolean authToBoolean : AuthBoolean.values()) {
            if (authToBoolean.getValue().toLowerCase().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
