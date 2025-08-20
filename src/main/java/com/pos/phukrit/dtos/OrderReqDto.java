package com.pos.phukrit.dtos;

import lombok.Data;
import java.util.List;

@Data
public class OrderReqDto {
    private List<OrderItemReqDto> items;
    private String customerPhoneNumber; // Can be null for guest checkouts
}