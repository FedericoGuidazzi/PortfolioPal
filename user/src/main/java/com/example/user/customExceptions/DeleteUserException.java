package com.example.user.customExceptions;

public class DeleteUserException extends Exception {
    public DeleteUserException(String message) {
        super(message);
    }

    public DeleteUserException(Throwable cause) {
        super(cause);
    }
}
