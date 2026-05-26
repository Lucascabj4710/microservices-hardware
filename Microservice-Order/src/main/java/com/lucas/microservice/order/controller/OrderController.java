package com.lucas.microservice.order.controller;

import com.lucas.microservice.order.dto.OrderItemDto;
import com.lucas.microservice.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody List<OrderItemDto> orderItemDtos){
        orderService.createOrder(orderItemDtos);
        return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(){
        return new ResponseEntity<>(orderService.getOrders(), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status){
        return new ResponseEntity<>(orderService.getOrdersByStatus(status), HttpStatus.OK);
    }

    @PutMapping("/{idOrder}")
    public ResponseEntity<?> canceledOrder(@PathVariable Long idOrder, @RequestParam String status){
        orderService.canceledOrder(status, idOrder);
        return new ResponseEntity<>("CANCELED ORDER", HttpStatus.OK);
    }

}
