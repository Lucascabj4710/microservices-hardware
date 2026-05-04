package com.lucas.microservice.product.controller;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.entities.Product;
import com.lucas.microservice.product.services.ProductService;
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(){
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto){
        productService.addProduct(productDto);

        return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
    }

    @PutMapping("/{idProduct}")
    public ResponseEntity<?> editProduct(@PathVariable Long idProduct, @RequestBody ProductDto productDto){
        productService.editProduct(idProduct, productDto);

        return new ResponseEntity<>("UPDATED", HttpStatus.OK);
    }

    @PatchMapping("/{status}")
    public ResponseEntity<?> editStatus(@PathVariable Boolean status, @PathVariable Long idProduct){
        productService.changeStatusProduct(idProduct, status);

        return new ResponseEntity<>("Changed Status", HttpStatus.OK);
    }




}
