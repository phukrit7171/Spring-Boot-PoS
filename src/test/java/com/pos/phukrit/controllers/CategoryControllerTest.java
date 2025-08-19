package com.pos.phukrit.controllers;

import java.lang.reflect.Field;
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
import com.pos.phukrit.models.Category;
import com.pos.phukrit.services.CategoryService;
import com.pos.phukrit.services.OrderService;
import com.pos.phukrit.services.ProductService;
import com.pos.phukrit.services.UserService;

@WebMvcTest(
    controllers = CategoryController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        ProductController.class,
        OrderController.class,
        UserController.class,
        AuthController.class
    })
)
@AutoConfigureMockMvc(addFilters = false)
@Import({})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private com.pos.phukrit.config.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductController productController;
    @MockBean
    private OrderController orderController;
    @MockBean
    private UserController userController;
    @MockBean
    private AuthController authController;

    

    @Test
    void getAllCategories_returnsList() throws Exception {
        Category c1 = new Category();
        setField(c1, "id", 1L);
        setField(c1, "name", "Electronics");
        Category c2 = new Category();
        setField(c2, "id", 2L);
        setField(c2, "name", "Clothing");
        when(categoryService.getAllCategories()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getCategoryById_foundOrNotFound() throws Exception {
        Category c = new Category();
        setField(c, "id", 10L);
        setField(c, "name", "Books");
        when(categoryService.getCategoryById(10L)).thenReturn(Optional.of(c));
        when(categoryService.getCategoryById(11L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L));

        mockMvc.perform(get("/api/categories/11"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_returnsCreated() throws Exception {
        Category payload = new Category();
        setField(payload, "name", "New Category");

        Category created = new Category();
        setField(created, "id", 3L);
        setField(created, "name", "New Category");
        when(categoryService.createCategory(any(Category.class))).thenReturn(created);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void updateAndDelete_category() throws Exception {
        Category details = new Category();
        setField(details, "name", "Updated Category");
        Category updated = new Category();
        setField(updated, "id", 4L);
        setField(updated, "name", "Updated Category");
        when(categoryService.updateCategory(eq(4L), any(Category.class))).thenReturn(Optional.of(updated));
        when(categoryService.updateCategory(eq(6L), any(Category.class))).thenReturn(Optional.empty());
        when(categoryService.deleteCategory(4L)).thenReturn(true);
        when(categoryService.deleteCategory(6L)).thenReturn(false);

        mockMvc.perform(put("/api/categories/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4L))
            .andExpect(jsonPath("$.name").value("Updated Category"));

        mockMvc.perform(put("/api/categories/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/categories/4"))
            .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/categories/6"))
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