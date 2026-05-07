package com.lucas.microservice.order.client;

import com.lucas.microservice.order.entities.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "microservice-product")
public interface ProductClient {

    @GetMapping("/product/id/{id}")
    Product getProduct(@PathVariable Long id);

    @GetMapping("/product/name/{name}")
    Product getProductByName(@PathVariable String name);

    @GetMapping("/product")
    List<Product> getAllProducts();
}
