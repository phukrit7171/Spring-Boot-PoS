package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.OrderDto;
import com.pos.phukrit.dtos.OrderItemDto;
import com.pos.phukrit.models.Order;
import com.pos.phukrit.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    OrderDto toDto(Order order);
    
    @Mapping(target = "user.id", source = "userId")
    Order toEntity(OrderDto orderDto);
    
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemDto toDto(OrderItem orderItem);
    
    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "product.id", source = "productId")
    OrderItem toEntity(OrderItemDto orderItemDto);
}