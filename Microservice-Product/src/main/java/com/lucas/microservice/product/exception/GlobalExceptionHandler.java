package com.lucas.microservice.product.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidProductStateException.class)
    public ResponseEntity<ApiError> handleInvalidProductStateException(InvalidProductStateException exception){
        ApiError apiError = new ApiError();

        apiError.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(exception.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStockException.class)
    public ResponseEntity<ApiError> handleInvalidStockException(InvalidStockException exception){
        ApiError apiError = new ApiError();

        apiError.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(exception.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFoundException(ProductNotFoundException exception) {
        ApiError apiError = new ApiError();

        apiError.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setMessage(exception.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception){

        ApiError apiError = new ApiError();

        apiError.setTimestamp(LocalDateTime.now());
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());

        String message = exception.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        apiError.setMessage(message);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
