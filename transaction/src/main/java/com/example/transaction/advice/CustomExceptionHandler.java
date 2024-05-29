package com.example.transaction.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.transaction.custom_exceptions.CustomException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.class)
    public Map<String, String> handleCustomdException(CustomException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", exception.getMessage());
        return map;
    }
}