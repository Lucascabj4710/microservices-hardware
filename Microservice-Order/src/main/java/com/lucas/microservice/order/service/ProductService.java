package com.lucas.microservice.order.service;

import com.lucas.microservice.order.client.ProductClient;
import com.lucas.microservice.order.dto.StockRequest;
import com.lucas.microservice.order.entities.Product;
import com.lucas.microservice.order.exception.ProductServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Slf4j
public class ProductService {

    private final ProductClient productClient;

    public ProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public Product fallbackGetProduct(Long id, Throwable throwable){
        log.error("Error al intentar obtener productos, {}", throwable.getMessage());

        throw new ProductServiceUnavailableException("El catálogo de productos no está disponible para consultar el ID: " + id);
    }

    public List<Product> fallbackGetProductsByIds(List<Long> ids, Throwable throwable){
        throw new ProductServiceUnavailableException("El catálogo de productos no está disponible para consultar");
    }

    public Product fallbackGetProductByName(String name, Throwable throwable){
        throw new ProductServiceUnavailableException("El catalogo de productos no esta disponible para consultas mediante nombre");
    }

    public void fallbackAddStock(List<StockRequest> stockRequests, Throwable throwable){
        throw new ProductServiceUnavailableException("El servicio productos no se encuentra disponible actualmente");
    }

    public void fallbackDiscountStock( List<StockRequest> stockRequests, Throwable throwable){
        throw new ProductServiceUnavailableException("El servicio productos no se encuentra disponible actualmente");
    }

    @Transactional
    @CircuitBreaker(name = "microservice-product", fallbackMethod = "fallbackGetProduct")
    @Retry(name = "microservice-product")
    public Product getProduct(Long id){
        return productClient.getProduct(id);
    }

    @Transactional
    @CircuitBreaker(name = "microservice-product", fallbackMethod = "fallbackGetProductsByIds")
    @Retry(name = "microservice-product")
    public List<Product> getProductsByIds(List<Long> ids){
        return productClient.getProductsByIds(ids);
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "microservice-product", fallbackMethod = "fallbackGetProductByName")
    @Retry(name = "microservice-product")
    public Product getProductByName(String name){
        return productClient.getProductByName(name);
    }

    @Transactional
    @CircuitBreaker(name = "microservice-product", fallbackMethod = "fallbackAddStock")
    @Retry(name = "microservice-product")
    public void addStock(List<StockRequest> stockRequests){
        productClient.addStock(stockRequests);
    }

    @Transactional
    @CircuitBreaker(name = "microservice-product", fallbackMethod = "fallbackDiscountStock")
    @Retry(name = "microservice-product")
    public void discountStock( List<StockRequest> stockRequests){
        productClient.discountStock(stockRequests);
    }

}
