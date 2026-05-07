package com.lucas.microservice.product.exception;

public class InvalidProductStateException extends RuntimeException {
    public InvalidProductStateException(String message) {
        super(message);
    }
}
