package com.pos.phukrit.services;

import com.pos.phukrit.models.Order;
import com.pos.phukrit.models.OrderItem;
import com.pos.phukrit.models.OrderStatus;
import com.pos.phukrit.repositories.OrderItemRepository;
import com.pos.phukrit.repositories.OrderRepository;
import com.pos.phukrit.repositories.ProductRepository;
import com.pos.phukrit.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_setsDefaults_calculatesTotal_andSetsBackReference() {
        Order order = new Order();
        OrderItem i1 = new OrderItem();
        i1.setQuantity(2);
        i1.setUnitPrice(new BigDecimal("6.25"));
        i1.setTotalPrice(new BigDecimal("12.50"));
        OrderItem i2 = new OrderItem();
        i2.setQuantity(1);
        i2.setUnitPrice(new BigDecimal("7.50"));
        i2.setTotalPrice(new BigDecimal("7.50"));
        
        // Set the order items properly
        order.getOrderItems().add(i1);
        order.getOrderItems().add(i2);
    
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
    
        Order saved = orderService.createOrder(order);
    
        assertThat(saved.getOrderDate()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(saved.getOrderItems()).hasSize(2);
        assertThat(saved.getOrderItems().get(0).getOrder()).isSameAs(saved);
        assertThat(saved.getOrderItems().get(1).getOrder()).isSameAs(saved);
    }

    @Test
    void updateAndDelete_behaviors() {
        Order existing = new Order();
        existing.setId(1L);
        existing.setOrderDate(LocalDateTime.now());
        existing.setStatus(OrderStatus.PENDING);
        existing.setTotalAmount(BigDecimal.ZERO);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order details = new Order();
        details.setStatus(OrderStatus.COMPLETED);

        assertThat(orderService.updateOrder(1L, details)).isPresent();
        assertThat(orderService.deleteOrder(1L)).isTrue();
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        assertThat(orderService.updateOrder(2L, details)).isEmpty();
        assertThat(orderService.deleteOrder(2L)).isFalse();
    }

    @Test
    void queries_delegateToRepository() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));
        when(orderRepository.findById(9L)).thenReturn(Optional.of(new Order()));
        when(orderRepository.findByUserId(5L)).thenReturn(List.of(new Order(), new Order()));
        when(orderRepository.findByStatus("PENDING")).thenReturn(List.of(new Order()));

        assertThat(orderService.getAllOrders()).hasSize(1);
        assertThat(orderService.getOrderById(9L)).isPresent();
        assertThat(orderService.getOrdersByUser(5L)).hasSize(2);
        assertThat(orderService.getOrdersByStatus("PENDING")).hasSize(1);
    }
}
