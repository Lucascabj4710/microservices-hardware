package com.lucas.microservice.order.config;

import com.lucas.microservice.order.exception.InsufficientStockException;
import com.lucas.microservice.order.exception.ProductNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new InsufficientStockException("Insufficient stock to process the order");
            case 404 -> new ProductNotFoundException("The request product does not exist in the catalog");
            case 500 -> new RuntimeException("Service unavailable");
            default -> new RuntimeException("Unexpected error: " + response.status());
        };
    }
}
