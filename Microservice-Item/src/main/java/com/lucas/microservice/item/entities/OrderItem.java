package com.lucas.microservice.item.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private Integer quantity;

    private Double unitPrice;

    private Double totalPrice;

    @PrePersist
    @PreUpdate
    private void calculateTotalPrice(){
        if (this.quantity != null && this.unitPrice != null) {
            this.totalPrice = this.quantity * this.unitPrice;
        }
    }
}
