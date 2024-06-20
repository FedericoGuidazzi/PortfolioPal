package com.example.user.customExceptions;

public class CustomUserException extends Exception{
    public CustomUserException(String message) {
        super(message);
    }

    public CustomUserException(Throwable cause) {
        super(cause);
    }
}
