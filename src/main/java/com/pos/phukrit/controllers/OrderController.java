package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.OrderReqDto;
import com.pos.phukrit.dtos.OrderResDto;
import com.pos.phukrit.services.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders - Create a new order
    @PostMapping
    public OrderResDto createOrder(@RequestBody OrderReqDto orderReqDto) {
        return orderService.createOrder(orderReqDto);
    }

    // We can add GET endpoints here later to view order history
}