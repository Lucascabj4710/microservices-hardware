package com.lucas.microservice.order.service;

import com.lucas.microservice.order.dto.OrderItemDto;
import com.lucas.microservice.order.dto.StockRequest;
import com.lucas.microservice.order.entities.Order;
import com.lucas.microservice.order.entities.OrderItem;
import com.lucas.microservice.order.entities.StatusOrder;
import com.lucas.microservice.order.exception.InvalidStatusException;
import com.lucas.microservice.order.mapper.OrderItemMapper;
import com.lucas.microservice.order.repository.OrderItemRepository;
import com.lucas.microservice.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Slf4j
public class OrderService {

    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderService(ProductService productService, OrderItemRepository orderItemRepository, OrderRepository orderRepository, OrderItemMapper orderItemMapper) {
        this.productService = productService;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Transactional
    public void createOrder(List<OrderItemDto> orderItemDtos){

        Order order = new Order();
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = orderItemDtos.stream().map(orderItemMapper::toOrderItem).toList();

        Double totalPrice = 0.0;

        for (OrderItem orderItem : orderItems){
            StockRequest stockRequest = new StockRequest();

            totalPrice += orderItem.getUnitPrice() * orderItem.getQuantity();
            orderItem.setOrder(savedOrder);

            stockRequest.setIdProduct(orderItem.getProductId());
            stockRequest.setQuantity(orderItem.getQuantity());

            productService.discountStock(stockRequest);

        }

        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setStatusOrder(StatusOrder.IN_PROCESS);
        orderRepository.save(savedOrder);
        orderItemRepository.saveAll(orderItems);
    }

    public List<Order> getOrders(){
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(String status){

        if (status == null) {
            throw new InvalidStatusException("Status cannot be null");
        }

        try {
            StatusOrder statusOrder = StatusOrder.valueOf(status.toUpperCase());

            return orderRepository.findByStatusOrder(statusOrder);

        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status");
        }
    }

}
