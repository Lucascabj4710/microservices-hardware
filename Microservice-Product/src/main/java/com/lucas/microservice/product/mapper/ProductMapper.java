package com.lucas.microservice.product.mapper;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.dto.ProductDtoResponse;
import com.lucas.microservice.product.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductDto productDto);

    ProductDto toProductDto(Product product);

    ProductDtoResponse toProductDtoResponse(Product product);

}
