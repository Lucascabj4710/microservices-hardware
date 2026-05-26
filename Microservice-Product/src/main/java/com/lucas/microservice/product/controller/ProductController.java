package com.lucas.microservice.product.controller;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.dto.ProductDtoResponse;
import com.lucas.microservice.product.dto.StockRequest;
import com.lucas.microservice.product.entities.Product;
import com.lucas.microservice.product.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/id/{id}")
    public ProductDtoResponse getProductById(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @PostMapping("/ids")
    public ResponseEntity<List<ProductDtoResponse>> getProductsByIds(@RequestBody List<Long> ids){
        return new ResponseEntity<>(productService.getProductsByIds(ids), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductDtoResponse> getProductByName(@PathVariable String name){
        return new ResponseEntity<>(productService.getProductByName(name), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductDtoResponse>> getProducts(){
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    @GetMapping("search/{value}")
    public ResponseEntity<?> getProductByValue(@PathVariable String value){
        return new ResponseEntity<>(productService.searchAvailableProductsByNameOrBrand(value), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductDto productDto){
        productService.addProduct(productDto);

        return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
    }

    @PutMapping("/{idProduct}")
    public ResponseEntity<?> editProduct(@PathVariable Long idProduct, @RequestBody ProductDto productDto){
        productService.editProduct(idProduct, productDto);

        return new ResponseEntity<>("UPDATED", HttpStatus.OK);
    }

    @PutMapping("/{idProduct}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long idProduct){

        productService.toggleStatusProduct(idProduct);

        return new ResponseEntity<>("Status changed", HttpStatus.OK);
    }

    @PutMapping("/addStock")
    public ResponseEntity<?> addStock(@Valid @RequestBody List<StockRequest> stockRequests){
        productService.addStock(stockRequests);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/discountStock")
    public ResponseEntity<?> discountStock(@Valid @RequestBody List<StockRequest> stockRequests){
        productService.discountStock(stockRequests);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
