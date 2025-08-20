package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.OrderResDto;
import com.pos.phukrit.dtos.OrderItemResDto;
import com.pos.phukrit.models.OrderModel;
import com.pos.phukrit.models.OrderItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "customer.id", target = "customerId") // Map customer ID
    @Mapping(source = "customer.name", target = "customerName") // Map customer name
    OrderResDto toOrderResDto(OrderModel orderModel);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResDto toOrderItemResDto(OrderItemModel orderItemModel);
}