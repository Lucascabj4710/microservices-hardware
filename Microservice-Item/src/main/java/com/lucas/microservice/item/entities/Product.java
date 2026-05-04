package com.lucas.microservice.item.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String brand;
    private Integer stock;
    private Double price;
    private Boolean available;

}
