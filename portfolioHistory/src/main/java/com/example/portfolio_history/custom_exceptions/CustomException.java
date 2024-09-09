package com.example.portfolio_history.custom_exceptions;

public class CustomException extends Exception{
    public CustomException(String message) {
        super(message);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
