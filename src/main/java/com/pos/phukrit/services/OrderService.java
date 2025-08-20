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
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel employee = userRepository.findByUsername(username)
                .orElse(null); // Null for anonymous/self-checkout

        CustomerModel customer = null;
        if (orderReqDto.getCustomerPhoneNumber() != null && !orderReqDto.getCustomerPhoneNumber().isEmpty()) {
            customer = customerRepository.findByPhoneNumber(orderReqDto.getCustomerPhoneNumber())
                    .orElse(null);
        }

        OrderModel order = new OrderModel();
        order.setUser(employee);
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderModel.OrderStatus.COMPLETED);

        List<OrderItemModel> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemReqDto itemDto : orderReqDto.getItems()) {
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

        if (customer != null) {
            int pointsEarned = (int) Math.floor(totalPrice);
            customer.setPoints(customer.getPoints() + pointsEarned);
        }

        OrderModel savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResDto(savedOrder);
    }
}