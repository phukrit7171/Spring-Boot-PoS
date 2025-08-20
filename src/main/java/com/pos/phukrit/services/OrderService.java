package com.pos.phukrit.services;

import com.pos.phukrit.dtos.OrderReqDto;
import com.pos.phukrit.dtos.OrderItemReqDto;
import com.pos.phukrit.dtos.OrderResDto;
import com.pos.phukrit.mappers.OrderMapper;
import com.pos.phukrit.models.OrderModel;
import com.pos.phukrit.models.OrderItemModel;
import com.pos.phukrit.models.ProductModel;
import com.pos.phukrit.repositories.OrderRepository;
import com.pos.phukrit.repositories.ProductRepository; // Still need this for getting product details
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository; // Field is now final
    private final ProductRepository productRepository; // Field is now final
    private final ProductService productService; // Field is now final
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    // All dependencies are injected via the constructor
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public OrderResDto createOrder(OrderReqDto orderReqDto) {
        OrderModel order = new OrderModel();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderModel.OrderStatus.COMPLETED);

        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemReqDto itemDto : orderReqDto.getItems()) {
            // --- ENHANCEMENT ---
            // The OrderService now delegates the responsibility of stock management.
            productService.reduceStock(itemDto.getProductId(), itemDto.getQuantity());

            // We still need to fetch the product to get its name and price for the order item
            ProductModel product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice()); // Price at time of sale
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            totalPrice += product.getPrice() * itemDto.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        OrderModel savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResDto(savedOrder);
    }
}