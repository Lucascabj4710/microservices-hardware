package com.lucas.microservice.product.services;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.dto.ProductDtoResponse;
import com.lucas.microservice.product.entities.Product;
import com.lucas.microservice.product.exception.InvalidProductStateException;
import com.lucas.microservice.product.exception.InvalidStockException;
import com.lucas.microservice.product.exception.ProductNotFoundException;
import com.lucas.microservice.product.mapper.ProductMapper;
import com.lucas.microservice.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductDtoResponse getProduct(Long id){
        return productRepository.findById(id)
                .map(productMapper::toProductDtoResponse)
                .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public ProductDtoResponse getProductByName(String name){
        return productRepository.findByName(name).map(productMapper::toProductDtoResponse)
                .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado con el nombre " + name));
    }


    @Transactional(readOnly = true)
    public List<ProductDtoResponse> getProducts(){
        return productRepository.findAll()
                .stream().map(productMapper::toProductDtoResponse)
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

    @Transactional
    public void addStock(Long idProduct, Integer quantity){

        if (quantity == null || quantity <= 0) {
            throw new InvalidStockException("La cantidad ha ingresar debe ser mnyor a 0 ");
        }

        Product product = findProductOrThrow(idProduct);

        if (product.getStock() == null) {
            throw new InvalidProductStateException("El stock del producto no puede ser nulo");
        }

        product.setStock(product.getStock() + quantity);

        productRepository.save(product);
    }


    public Product findProductOrThrow(Long idProduct){
        return productRepository.findById(idProduct)
                .orElseThrow(()-> new ProductNotFoundException("El producto con el id solicitado, no existe"));
    }

}
