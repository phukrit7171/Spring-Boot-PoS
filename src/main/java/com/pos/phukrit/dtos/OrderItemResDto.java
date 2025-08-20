package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class OrderItemResDto {
    private Long id;
    private Long productId; // Just the ID is often enough
    private String productName; // But including the name is user-friendly
    private int quantity;
    private double price; // The price at the time of sale
}