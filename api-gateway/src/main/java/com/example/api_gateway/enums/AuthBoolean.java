package com.example.api_gateway.enums;

public enum AuthBoolean {
    
    TRUE("true"),
    FALSE("false");

    private final String value;

    AuthBoolean(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean fromValue(String value) {
        for (AuthBoolean authBoolean : AuthBoolean.values()) {
            if (authBoolean.getValue().toLowerCase().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
