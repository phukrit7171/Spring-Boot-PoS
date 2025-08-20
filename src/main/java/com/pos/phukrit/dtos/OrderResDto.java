package com.pos.phukrit.dtos;

import com.pos.phukrit.models.OrderModel;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResDto {
    private Long id;
    private Long userId; // Employee who made the sale
    private String username;
    private Long customerId; // Customer who the sale was for (optional)
    private String customerName;
    private LocalDateTime orderDate;
    private Double totalPrice;
    private OrderModel.OrderStatus status;
    private List<OrderItemResDto> items;
}