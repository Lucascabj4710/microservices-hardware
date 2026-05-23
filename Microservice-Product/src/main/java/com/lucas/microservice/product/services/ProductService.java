package com.lucas.microservice.product.services;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.dto.ProductDtoResponse;
import com.lucas.microservice.product.dto.StockRequest;
import com.lucas.microservice.product.entities.Product;
import com.lucas.microservice.product.exception.InsufficientStockException;
import com.lucas.microservice.product.exception.InvalidProductStateException;
import com.lucas.microservice.product.exception.ProductNotFoundException;
import com.lucas.microservice.product.mapper.ProductMapper;
import com.lucas.microservice.product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductDtoResponse getProduct(Long id){

        log.warn("INICIANDO METODO GET PRODUCT");

        return productRepository.findById(id)
                .map(productMapper::toProductDtoResponse)
                .orElseThrow(()-> new ProductNotFoundException("Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public ProductDtoResponse getProductByName(String name){

        log.warn("INICIANDO METODO GET PRODUCT BY NAME");

        return productRepository.findByName(name).map(productMapper::toProductDtoResponse)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with name: " + name));
    }

    @Transactional(readOnly = true)
    public List<ProductDtoResponse> searchAvailableProductsByNameOrBrand(String value){
        return productRepository.searchByNameOrBrandAndAvailableTrue(value)
                .stream()
                .map(productMapper::toProductDtoResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<ProductDtoResponse> getProducts(){

        log.warn("INICIANDO METODO GET PRODUCTS");

        return productRepository.findAll()
                .stream().map(productMapper::toProductDtoResponse)
                .toList();
    }

    @Transactional
    public void addProduct(ProductDto productDto){

        log.warn("INICIANDO METODO ADD PRODUCT");

        Product product = productMapper.toProduct(productDto);

        productRepository.save(product);
    }

    @Transactional
    public void editProduct(Long idProduct, ProductDto productDto){

        log.warn("INICIANDO METODO EDIT PRODUCT");

        Product product = findProductOrThrow(idProduct);

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());

        productRepository.save(product);
    }

    @Transactional
    public void toggleStatusProduct(Long idProduct){

        log.warn("INICIANDO METODO TOGGLE STATUS PRODUCT");

        Product product = findProductOrThrow(idProduct);

        product.setAvailable(!product.getAvailable());

        productRepository.save(product);
    }

    @Transactional
    public void addStock(StockRequest stockRequest){

        log.warn("INICIANDO METODO ADD STOCK");

        Product product = findProductOrThrow(stockRequest.getIdProduct());

        if (product.getStock() == null) {
            throw new InvalidProductStateException("El stock del producto no puede ser nulo");
        }

        product.setStock(product.getStock() + stockRequest.getQuantity());

        productRepository.save(product);
    }

    @Transactional
    public void discountStock(List<StockRequest> stockRequests){

        log.warn("INICIANDO METODO DISCOUNT STOCK");

        List<Long> idsProducts = stockRequests.stream()
                .map(StockRequest::getIdProduct)
                .toList();

        List<Product> products = productRepository.findProductByIdIn(idsProducts);
        Map<Long, Integer> quantityProducts = stockRequests.stream()
                .collect(Collectors.toMap(
                        stockRequest -> stockRequest.getIdProduct(),
                        stockRequest -> stockRequest.getQuantity(),
                        (existingQuantity, newQuantity) -> existingQuantity + newQuantity
                ));


        for (Product product : products){

            log.info("Entidad cargada : {}", product.getName());

            if (product.getStock() == null) {
                throw new InvalidProductStateException("El stock del producto no puede ser nulo");
            }

            Integer newStock = product.getStock() - quantityProducts.get(product.getId());

            log.info("New STOCK : {}", newStock);

            if (newStock < 0) {
                throw new InsufficientStockException("Insufficient stock");
            }

            product.setStock(newStock);
        }

        productRepository.saveAll(products);
    }

    @Transactional
    public List<ProductDtoResponse> getProductsByIds(List<Long> ids){
        return productRepository.findProductByIdIn(ids)
                .stream().map(productMapper::toProductDtoResponse)
                .toList();
    }



    public Product findProductOrThrow(Long idProduct){
        return productRepository.findById(idProduct)
                .orElseThrow(()-> new ProductNotFoundException("El producto con el id solicitado, no existe"));
    }

}
