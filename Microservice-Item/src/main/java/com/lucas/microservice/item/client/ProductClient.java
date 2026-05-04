package com.lucas.microservice.item.client;

import com.lucas.microservice.item.entities.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "microservice-product")
public interface ProductClient {

    @GetMapping("/product/{id}")
    Product getProduct(@PathVariable Long id);

    @GetMapping("/product")
    List<Product> getAllProducts();

}
