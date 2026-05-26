package com.lucas.microservice.order.service;

import com.lucas.microservice.order.client.ProductClient;
import com.lucas.microservice.order.dto.StockRequest;
import com.lucas.microservice.order.entities.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductClient productClient;

    public ProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Transactional
    public Product getProduct(Long id){
        return productClient.getProduct(id);
    }

    @Transactional
    public List<Product> getProductsByIds(List<Long> ids){
        return productClient.getProductsByIds(ids);
    }

    @Transactional(readOnly = true)
    public Product getProductByName(String name){
        return productClient.getProductByName(name);
    }

    @Transactional
    public void addStock(List<StockRequest> stockRequests){
        productClient.addStock(stockRequests);
    }

    @Transactional
    public void discountStock( List<StockRequest> stockRequests){
        productClient.discountStock(stockRequests);
    }

}
