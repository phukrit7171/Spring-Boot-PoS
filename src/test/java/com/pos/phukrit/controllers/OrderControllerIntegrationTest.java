package com.pos.phukrit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.phukrit.models.CustomerModel;
import com.pos.phukrit.models.ProductModel;
import com.pos.phukrit.repositories.CustomerRepository;
import com.pos.phukrit.repositories.OrderRepository;
import com.pos.phukrit.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ProductModel testProduct;

    @BeforeEach
    void setUp() {
        // Clean all repositories to ensure a fresh state for each test
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        // Arrange: Create sample data in the database
        testProduct = new ProductModel();
        testProduct.setName("Latte");
        testProduct.setPrice(4.00);
        testProduct.setStock(20);
        productRepository.save(testProduct);

        CustomerModel testCustomer = new CustomerModel();
        testCustomer.setName("Jane Smith");
        testCustomer.setPhoneNumber("0987654321");
        testCustomer.setPoints(10);
        customerRepository.save(testCustomer);
    }

    @Test
    @WithMockUser(username = "staff", roles = "STAFF") // Simulate a logged-in staff member
    void createOrder_withExistingCustomer_shouldSucceed() throws Exception {
        // 1. Arrange: Create the request body for the order
        String orderJson = String.format("""
            {
                "items": [
                    { "productId": %d, "quantity": 2 }
                ],
                "customerPhoneNumber": "0987654321"
            }
            """, testProduct.getId());

        // 2. Act & Assert: Perform the POST request
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(8.00))
                .andExpect(jsonPath("$.customerName").value("Jane Smith"));

        // 3. Verify the side-effects in the database
        // Verify stock was reduced
        ProductModel updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertEquals(18, updatedProduct.getStock());

        // Verify points were awarded (1 point per dollar)
        CustomerModel updatedCustomer = customerRepository.findByPhoneNumber("0987654321").orElseThrow();
        assertEquals(18, updatedCustomer.getPoints()); // 10 original + 8 from sale
    }

    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    void createOrder_asGuest_shouldSucceed() throws Exception {
        // Arrange: Order without a customer phone number
        String orderJson = String.format("""
            {
                "items": [
                    { "productId": %d, "quantity": 1 }
                ]
            }
            """, testProduct.getId());

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(4.00))
                .andExpect(jsonPath("$.customerName").isEmpty());

        // Verify stock was reduced
        ProductModel updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertEquals(19, updatedProduct.getStock());
    }
}