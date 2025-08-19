package com.pos.phukrit.services;

import com.pos.phukrit.models.Order;
import com.pos.phukrit.models.OrderItem;
import com.pos.phukrit.models.OrderStatus;
import com.pos.phukrit.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Order createOrder(Order order) {
        // Set order date if not provided
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        
        // Set initial status if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        
        // Calculate total amount if not provided
        if (order.getTotalAmount() == null) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : order.getOrderItems()) {
                total = total.add(item.getTotalPrice());
            }
            order.setTotalAmount(total);
        }
        
        // Set order reference in order items
        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order);
        }
        
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Long id, Order orderDetails) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(orderDetails.getStatus());
            return orderRepository.save(order);
        });
    }

    public boolean deleteOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }
}