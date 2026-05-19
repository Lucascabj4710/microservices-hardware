package com.lucas.microservice.order.repository;

import com.lucas.microservice.order.entities.Order;
import com.lucas.microservice.order.entities.StatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusOrder(StatusOrder statusOrder);

}
