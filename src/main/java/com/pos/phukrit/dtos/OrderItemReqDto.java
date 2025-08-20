package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class OrderItemReqDto {
    private Long productId;
    private Integer quantity;
}