package com.lucas.microservice.order.client;

import com.lucas.microservice.order.dto.StockRequest;
import com.lucas.microservice.order.entities.Product;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservice-product")
public interface ProductClient {

    @GetMapping("/product/id/{id}")
    Product getProduct(@PathVariable Long id);

    @PostMapping("/product/ids")
    List<Product> getProductsByIds(@RequestBody List<Long> ids);

    @GetMapping("/product/name/{name}")
    Product getProductByName(@PathVariable String name);

    @PutMapping("/product/addStock")
    public ResponseEntity<?> addStock(@Valid @RequestBody List<StockRequest> stockRequests);

    @PutMapping("/product/discountStock")
    public ResponseEntity<?> discountStock(@Valid @RequestBody  List<StockRequest> stockRequests);



}
