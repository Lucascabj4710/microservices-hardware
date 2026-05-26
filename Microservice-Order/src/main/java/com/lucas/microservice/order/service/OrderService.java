package com.lucas.microservice.order.service;

import com.lucas.microservice.order.dto.OrderItemDto;
import com.lucas.microservice.order.dto.StockRequest;
import com.lucas.microservice.order.entities.Order;
import com.lucas.microservice.order.entities.OrderItem;
import com.lucas.microservice.order.entities.Product;
import com.lucas.microservice.order.entities.StatusOrder;
import com.lucas.microservice.order.exception.InvalidStatusException;
import com.lucas.microservice.order.exception.OrderNotFoundException;
import com.lucas.microservice.order.exception.ProductNotFoundException;
import com.lucas.microservice.order.mapper.OrderItemMapper;
import com.lucas.microservice.order.repository.OrderItemRepository;
import com.lucas.microservice.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Lista de Cantidades sin ID de productos repetidos
        Map<Long, Integer> quantityDistinct = orderItemDtos.stream()
                .collect(Collectors.toMap(OrderItemDto::getProductId,
                        OrderItemDto::getQuantity,
                        Integer::sum));


        List<Long> idsProducts = orderItemDtos.stream()
                .map(OrderItemDto::getProductId)
                .distinct()
                .toList();


        Map<Long,Product> products = productService.getProductsByIds(idsProducts)
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<StockRequest> stockRequestList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();

        if (products.size() < idsProducts.size()){
            throw new ProductNotFoundException("One or more requested products do not exist in the catalog.");
        }

        Double totalPrice = 0.0;

        for (Long idProduct : idsProducts){
            OrderItem orderItem = new OrderItem();
            StockRequest stockRequest = new StockRequest();

            Product product = products.get(idProduct);

            orderItem.setOrder(order);
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice() * quantityDistinct.get(product.getId()));
            orderItem.setQuantity(quantityDistinct.get(product.getId()));
            orderItem.setProductId(product.getId());

            stockRequest.setIdProduct(product.getId());
            stockRequest.setQuantity(quantityDistinct.get(product.getId()));

            stockRequestList.add(stockRequest);

            orderItemList.add(orderItem);
            totalPrice += product.getPrice() * quantityDistinct.get(product.getId());

        }

        productService.discountStock(stockRequestList);
        order.setTotalPrice(totalPrice);
        order.setStatusOrder(StatusOrder.IN_PROCESS);
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItemList);
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

    @Transactional
    public void canceledOrder(String status, Long idOrder){

        if (!status.toUpperCase().equals(StatusOrder.CANCELED.name())){
            throw new InvalidStatusException("Status INVALID");
        }

        Order order = orderRepository.findById(idOrder)
                .orElseThrow(()-> new OrderNotFoundException("The requested Order does not exists"));

        List<StockRequest> stockRequestList = orderItemRepository.findByOrder_Id(idOrder)
                        .stream()
                        .map(orderItem -> {
                            StockRequest stockRequest = new StockRequest();
                            stockRequest.setIdProduct(orderItem.getProductId());
                            stockRequest.setQuantity(orderItem.getQuantity());

                            return stockRequest;
                        }).toList();

        productService.addStock(stockRequestList);
        order.setStatusOrder(StatusOrder.CANCELED);

        orderRepository.save(order);

    }

}
