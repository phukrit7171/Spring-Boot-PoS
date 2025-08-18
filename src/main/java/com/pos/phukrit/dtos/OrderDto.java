package com.pos.phukrit.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private Long userId;
    private String username;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}