package com.pos.phukrit.dtos;

import com.pos.phukrit.models.OrderModel;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResDto {
    private Long id;
    private Long userId; // The ID of the user who made the order
    private String username; // The username for display
    private LocalDateTime orderDate;
    private Double totalPrice;
    private OrderModel.OrderStatus status;
    private List<OrderItemResDto> items; // A list of the items in the order
}