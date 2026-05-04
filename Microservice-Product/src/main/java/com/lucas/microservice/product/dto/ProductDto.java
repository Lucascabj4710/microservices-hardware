package com.lucas.microservice.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductDto {

    private String name;
    private String brand;
    private Integer stock;
    private Double price;
    private Boolean available;
}
