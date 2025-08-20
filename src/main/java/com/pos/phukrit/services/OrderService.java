package com.pos.phukrit.services;

import com.pos.phukrit.dtos.OrderReqDto;
import com.pos.phukrit.dtos.OrderItemReqDto;
import com.pos.phukrit.dtos.OrderResDto;
import com.pos.phukrit.mappers.OrderMapper;
import com.pos.phukrit.models.OrderModel;
import com.pos.phukrit.models.OrderItemModel;
import com.pos.phukrit.models.ProductModel;
import com.pos.phukrit.repositories.OrderRepository;
import com.pos.phukrit.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderResDto createOrder(OrderReqDto orderReqDto) {
        OrderModel order = new OrderModel();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderModel.OrderStatus.COMPLETED); // Let's set it to COMPLETED for simplicity

        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemReqDto itemDto : orderReqDto.getItems()) {
            ProductModel product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }

            product.setStock(product.getStock() - itemDto.getQuantity());
            // The transaction will ensure this save is committed only if the whole process succeeds

            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
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