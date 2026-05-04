package com.lucas.microservice.product.services;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.entities.Product;
import com.lucas.microservice.product.mapper.ProductMapper;
import com.lucas.microservice.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(Long id){
        return productRepository.findById(id)
                .map(productMapper::toProductDto)
                .orElseThrow(()-> new RuntimeException("Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(){
        return productRepository.findAll()
                .stream().map(productMapper::toProductDto)
                .toList();
    }

    @Transactional
    public void addProduct(ProductDto productDto){

        Product product = productMapper.toProduct(productDto);

        productRepository.save(product);
    }

    @Transactional
    public void editProduct(Long idProduct, ProductDto productDto){

        Product product = findProductOrThrow(idProduct);

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());

        productRepository.save(product);
    }

    @Transactional
    public void changeStatusProduct(Long idProduct, Boolean status){

        Product product = findProductOrThrow(idProduct);

        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }

        product.setAvailable(status);

        productRepository.save(product);
    }

    public Product findProductOrThrow(Long idProduct){
        return productRepository.findById(idProduct)
                .orElseThrow(()-> new NoSuchElementException("El producto con el id solicitado, no existe"));
    }

}
