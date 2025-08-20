package com.pos.phukrit.services;

import com.pos.phukrit.dtos.OrderReqDto;
import com.pos.phukrit.dtos.OrderItemReqDto;
import com.pos.phukrit.dtos.OrderResDto;
import com.pos.phukrit.mappers.OrderMapper;
import com.pos.phukrit.models.CustomerModel;
import com.pos.phukrit.models.OrderModel;
import com.pos.phukrit.models.OrderItemModel;
import com.pos.phukrit.models.ProductModel;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.repositories.CustomerRepository;
import com.pos.phukrit.repositories.OrderRepository;
import com.pos.phukrit.repositories.ProductRepository;
import com.pos.phukrit.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository; // Add CustomerRepository
    private final UserRepository userRepository;         // Add UserRepository
    private final ProductService productService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    // Update the constructor with new dependencies
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository,
                        UserRepository userRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    public OrderResDto createOrder(OrderReqDto orderReqDto) {
        // 1. Get the logged-in employee (UserModel)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found: " + username));

        // 2. Find the customer if a phone number is provided
        CustomerModel customer = null;
        if (orderReqDto.getCustomerPhoneNumber() != null && !orderReqDto.getCustomerPhoneNumber().isEmpty()) {
            customer = customerRepository.findByPhoneNumber(orderReqDto.getCustomerPhoneNumber())
                    .orElse(null); // Or throw an exception if customer must exist
        }

        OrderModel order = new OrderModel();
        order.setUser(employee); // Link the order to the employee
        order.setCustomer(customer); // Link to customer (can be null)
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderModel.OrderStatus.COMPLETED);

        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemReqDto itemDto : orderReqDto.getItems()) {
            // Delegate stock reduction to ProductService
            productService.reduceStock(itemDto.getProductId(), itemDto.getQuantity());

            ProductModel product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

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

        // 3. Award points if there is a customer
        // (e.g., 1 point for every dollar spent)
        if (customer != null) {
            int pointsEarned = (int) Math.floor(totalPrice);
            customer.setPoints(customer.getPoints() + pointsEarned);
            // customerRepository.save(customer) is not needed due to @Transactional
        }

        OrderModel savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResDto(savedOrder);
    }
}