package com.pos.phukrit.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Long orderId;
    private Long productId;
    private String productName;
}