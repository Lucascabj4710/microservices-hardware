package com.lucas.microservice.order.mapper;

import com.lucas.microservice.order.dto.OrderItemDto;
import com.lucas.microservice.order.entities.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toOrderItem(OrderItemDto orderItemDto);

    OrderItemDto toOrderItemDto(OrderItem orderItem);

}
