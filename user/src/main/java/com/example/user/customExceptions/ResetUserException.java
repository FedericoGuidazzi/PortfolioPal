package com.example.user.customExceptions;

public class ResetUserException extends Exception{
    public ResetUserException(String message) {
        super(message);
    }

    public ResetUserException(Throwable cause) {
        super(cause);
    }
}
