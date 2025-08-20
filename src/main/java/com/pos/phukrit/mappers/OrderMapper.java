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

    // --- Mappings for Order ---

    // This tells MapStruct where to get the userId and username from
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    OrderResDto toOrderResDto(OrderModel orderModel);


    // --- Mappings for OrderItem ---

    // This tells MapStruct where to get the productId and productName from
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResDto toOrderItemResDto(OrderItemModel orderItemModel);
}