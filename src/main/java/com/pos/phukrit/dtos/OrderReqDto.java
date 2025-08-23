package com.pos.phukrit.dtos;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;

@Data
public class OrderReqDto {
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemReqDto> items;
    
    private String customerPhoneNumber;
}