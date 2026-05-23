package com.lucas.microservice.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound(ProductNotFoundException productNotFoundException){
        return new ResponseEntity<>(productNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStock(InsufficientStockException insufficientStockException){
        return new ResponseEntity<>(insufficientStockException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<?> handleInvalidadStatus(InvalidStatusException invalidStatusException){
        return new ResponseEntity<>(invalidStatusException.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
