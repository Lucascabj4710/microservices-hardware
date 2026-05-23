package com.lucas.microservice.product.repositories;

import com.lucas.microservice.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findProductByIdIn(List<Long> ids);



    @Query("""
    SELECT p FROM Product p
    WHERE (LOWER(p.name) = LOWER(:value)
        OR LOWER(p.brand) = LOWER(:value))
    AND p.available = true
    """)
    List<Product> searchByNameOrBrandAndAvailableTrue(String value);

    List<Long> id(Long id);
}
