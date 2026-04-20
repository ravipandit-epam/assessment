package com.example.backend.exception;

public class CacheClientException extends RuntimeException {

    public CacheClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
