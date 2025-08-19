package com.pos.phukrit.controllers;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.phukrit.models.Order;
import com.pos.phukrit.models.OrderStatus;
import com.pos.phukrit.services.OrderService;
import com.pos.phukrit.services.UserService;

@WebMvcTest(
    controllers = OrderController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        ProductController.class,
        CategoryController.class,
        UserController.class,
        AuthController.class
    })
)
@AutoConfigureMockMvc(addFilters = false)
@Import({})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private com.pos.phukrit.config.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductController productController;
    @MockBean
    private CategoryController categoryController;
    @MockBean
    private UserController userController;
    @MockBean
    private AuthController authController;

    

    @Test
    void getAllOrders_returnsList() throws Exception {
        Order o1 = new Order();
        setField(o1, "id", 1L);
        setField(o1, "orderDate", LocalDateTime.now());
        setField(o1, "totalAmount", new BigDecimal("100.00"));
        setField(o1, "status", OrderStatus.PENDING);
        Order o2 = new Order();
        setField(o2, "id", 2L);
        setField(o2, "orderDate", LocalDateTime.now());
        setField(o2, "totalAmount", new BigDecimal("200.00"));
        setField(o2, "status", OrderStatus.COMPLETED);
        when(orderService.getAllOrders()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getOrderById_foundOrNotFound() throws Exception {
        Order o = new Order();
        setField(o, "id", 10L);
        setField(o, "orderDate", LocalDateTime.now());
        setField(o, "totalAmount", new BigDecimal("150.00"));
        setField(o, "status", OrderStatus.PENDING);
        when(orderService.getOrderById(10L)).thenReturn(Optional.of(o));
        when(orderService.getOrderById(11L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L));

        mockMvc.perform(get("/api/orders/11"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByUser_returnsList() throws Exception {
        Order o1 = new Order();
        setField(o1, "id", 1L);
        setField(o1, "orderDate", LocalDateTime.now());
        setField(o1, "totalAmount", new BigDecimal("100.00"));
        setField(o1, "status", OrderStatus.PENDING);
        Order o2 = new Order();
        setField(o2, "id", 2L);
        setField(o2, "orderDate", LocalDateTime.now());
        setField(o2, "totalAmount", new BigDecimal("200.00"));
        setField(o2, "status", OrderStatus.COMPLETED);
        when(orderService.getOrdersByUser(5L)).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/orders/user/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getOrdersByStatus_returnsList() throws Exception {
        Order o1 = new Order();
        setField(o1, "id", 1L);
        setField(o1, "orderDate", LocalDateTime.now());
        setField(o1, "totalAmount", new BigDecimal("100.00"));
        setField(o1, "status", OrderStatus.PENDING);
        Order o2 = new Order();
        setField(o2, "id", 2L);
        setField(o2, "orderDate", LocalDateTime.now());
        setField(o2, "totalAmount", new BigDecimal("200.00"));
        setField(o2, "status", OrderStatus.PENDING);
        when(orderService.getOrdersByStatus("PENDING")).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/orders/status/PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void createOrder_returnsCreated() throws Exception {
        Order payload = new Order();

        Order created = new Order();
        setField(created, "id", 3L);
        setField(created, "orderDate", LocalDateTime.now());
        setField(created, "totalAmount", new BigDecimal("150.00"));
        setField(created, "status", OrderStatus.PENDING);
        when(orderService.createOrder(any(Order.class))).thenReturn(created);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void updateAndDelete_order() throws Exception {
        Order details = new Order();
        setField(details, "status", OrderStatus.COMPLETED);
        Order updated = new Order();
        setField(updated, "id", 4L);
        setField(updated, "orderDate", LocalDateTime.now());
        setField(updated, "totalAmount", new BigDecimal("250.00"));
        setField(updated, "status", OrderStatus.COMPLETED);
        when(orderService.updateOrder(eq(4L), any(Order.class))).thenReturn(Optional.of(updated));
        when(orderService.updateOrder(eq(6L), any(Order.class))).thenReturn(Optional.empty());
        when(orderService.deleteOrder(4L)).thenReturn(true);
        when(orderService.deleteOrder(6L)).thenReturn(false);

        mockMvc.perform(put("/api/orders/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4L))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(put("/api/orders/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/orders/4"))
            .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/orders/6"))
            .andExpect(status().isNotFound());
    }
    
    // Helper method to set private fields using reflection
    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}