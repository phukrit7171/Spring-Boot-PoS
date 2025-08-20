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

    // We can inject UserRepository here later to assign orders to specific users

    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderResDto createOrder(OrderRequestDto orderRequestDto) {
        OrderModel order = new OrderModel();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderModel.OrderStatus.PENDING);

        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        // Loop through each item in the request
        for (OrderItemRequestDto itemDto : orderRequestDto.getItems()) {
            // 1. Find the product in the database
            ProductModel product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));

            // 2. Check if there is enough stock
            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // 3. Decrease the product's stock
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product); // Update the product in the DB

            // 4. Create a new OrderItem
            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice()); // Set price at time of sale
            orderItem.setOrder(order); // Link back to the main order
            orderItems.add(orderItem);

            // 5. Add to the total price
            totalPrice += product.getPrice() * itemDto.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Save the complete order with all its items
        OrderModel savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResDto(savedOrder);
    }
}