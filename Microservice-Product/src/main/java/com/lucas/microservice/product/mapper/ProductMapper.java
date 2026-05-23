package com.lucas.microservice.product.mapper;

import com.lucas.microservice.product.dto.ProductDto;
import com.lucas.microservice.product.dto.ProductDtoResponse;
import com.lucas.microservice.product.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serverPort", ignore = true)
    Product toProduct(ProductDto productDto);

    @Mapping(target = "serverPort", ignore = true)
    Product toProductFromProductDtoResponse(ProductDtoResponse productDtoResponse);

    ProductDto toProductDto(Product product);

    ProductDtoResponse toProductDtoResponse(Product product);

}
