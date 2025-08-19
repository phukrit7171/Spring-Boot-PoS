package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.OrderDto;
import com.pos.phukrit.dtos.OrderItemDto;
import com.pos.phukrit.models.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void order_toDto_and_back_mapsUserFields() {
        UserModel user = new UserModel();
        user.setId(11L);
        user.setUsername("jane");
        user.setRole(UserRole.CUSTOMER);
        user.setPassword("p");

        Order order = new Order();
        order.setId(5L);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        OrderDto dto = mapper.toDto(order);
        assertThat(dto.getUserId()).isEqualTo(11L);
        assertThat(dto.getUsername()).isEqualTo("jane");

        Order back = mapper.toEntity(dto);
        assertThat(back.getUser()).isNotNull();
        assertThat(back.getUser().getId()).isEqualTo(11L);
    }

    @Test
    void orderItem_toDto_and_back_mapsOrderAndProductFields() {
        Order order = new Order();
        order.setId(7L);
        Product product = new Product();
        product.setId(3L);
        product.setName("Chips");

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("2.50"));
        item.setTotalPrice(new BigDecimal("5.00"));

        OrderItemDto dto = mapper.toDto(item);
        assertThat(dto.getOrderId()).isEqualTo(7L);
        assertThat(dto.getProductId()).isEqualTo(3L);
        assertThat(dto.getProductName()).isEqualTo("Chips");

        OrderItem back = mapper.toEntity(dto);
        assertThat(back.getOrder()).isNotNull();
        assertThat(back.getOrder().getId()).isEqualTo(7L);
        assertThat(back.getProduct()).isNotNull();
        assertThat(back.getProduct().getId()).isEqualTo(3L);
    }
}
