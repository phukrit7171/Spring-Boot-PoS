package com.pos.phukrit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.phukrit.models.Product;
import com.pos.phukrit.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_returnsList() throws Exception {
        Product p1 = new Product(); p1.setId(1L); p1.setName("A");
        Product p2 = new Product(); p2.setId(2L); p2.setName("B");
        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getProductById_foundOrNotFound() throws Exception {
        Product p = new Product(); p.setId(10L); p.setName("X");
        when(productService.getProductById(10L)).thenReturn(Optional.of(p));
        when(productService.getProductById(11L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L));

        mockMvc.perform(get("/api/products/11"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByCategory_and_search() throws Exception {
        when(productService.getProductsByCategory(5L)).thenReturn(List.of(new Product()));
        when(productService.searchProductsByName("tea")).thenReturn(List.of(new Product(), new Product()));

        mockMvc.perform(get("/api/products/category/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/products/search").param("name", "tea"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void createProduct_returnsCreated() throws Exception {
        Product payload = new Product();
        payload.setName("New");
        payload.setPrice(new BigDecimal("9.99"));

        Product created = new Product(); created.setId(3L); created.setName("New");
        when(productService.createProduct(any(Product.class))).thenReturn(created);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void updateAndDelete_product() throws Exception {
        Product details = new Product(); details.setName("Updated");
        Product updated = new Product(); updated.setId(4L); updated.setName("Updated");
        when(productService.updateProduct(eq(4L), any(Product.class))).thenReturn(Optional.of(updated));
        when(productService.updateProduct(eq(6L), any(Product.class))).thenReturn(Optional.empty());
        when(productService.deleteProduct(4L)).thenReturn(true);
        when(productService.deleteProduct(6L)).thenReturn(false);

        mockMvc.perform(put("/api/products/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4L))
            .andExpect(jsonPath("$.name").value("Updated"));

        mockMvc.perform(put("/api/products/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/products/4"))
            .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/products/6"))
            .andExpect(status().isNotFound());
    }
}
