package com.example.camunda8warehouse.exceptions;

public class RetryableException extends RuntimeException {

    public RetryableException(String message) {
        super(message);
    }
}
