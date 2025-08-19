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
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.models.UserRole;
import com.pos.phukrit.services.UserService;

@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        ProductController.class,
        CategoryController.class,
        OrderController.class,
        AuthController.class
    })
)
@AutoConfigureMockMvc(addFilters = false)
@Import({})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private com.pos.phukrit.config.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private ProductController productController;
    @MockBean
    private CategoryController categoryController;
    @MockBean
    private OrderController orderController;
    @MockBean
    private AuthController authController;

    

    @Test
    void getAllUsers_returnsList() throws Exception {
        UserModel u1 = new UserModel();
        setField(u1, "id", 1L);
        setField(u1, "name", "User One");
        setField(u1, "username", "user1");
        setField(u1, "email", "user1@example.com");
        setField(u1, "role", UserRole.CUSTOMER);
        UserModel u2 = new UserModel();
        setField(u2, "id", 2L);
        setField(u2, "name", "User Two");
        setField(u2, "username", "user2");
        setField(u2, "email", "user2@example.com");
        setField(u2, "role", UserRole.STAFF);
        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getUserById_foundOrNotFound() throws Exception {
        UserModel u = new UserModel();
        setField(u, "id", 10L);
        setField(u, "name", "User Ten");
        setField(u, "username", "user10");
        setField(u, "email", "user10@example.com");
        setField(u, "role", UserRole.ADMIN);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u));
        when(userService.getUserById(11L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L));

        mockMvc.perform(get("/api/users/11"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        UserModel payload = new UserModel();
        setField(payload, "name", "New User");
        setField(payload, "username", "newuser");
        setField(payload, "email", "newuser@example.com");
        setField(payload, "password", "password");
        setField(payload, "role", UserRole.CUSTOMER);

        UserModel created = new UserModel();
        setField(created, "id", 3L);
        setField(created, "name", "New User");
        setField(created, "username", "newuser");
        setField(created, "email", "newuser@example.com");
        setField(created, "role", UserRole.CUSTOMER);
        when(userService.createUser(any(UserModel.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void updateAndDelete_user() throws Exception {
        UserModel details = new UserModel();
        setField(details, "name", "Updated User");
        setField(details, "username", "updateduser");
        setField(details, "email", "updateduser@example.com");
        UserModel updated = new UserModel();
        setField(updated, "id", 4L);
        setField(updated, "name", "Updated User");
        setField(updated, "username", "updateduser");
        setField(updated, "email", "updateduser@example.com");
        setField(updated, "role", UserRole.CUSTOMER);
        when(userService.updateUser(eq(4L), any(UserModel.class))).thenReturn(Optional.of(updated));
        when(userService.updateUser(eq(6L), any(UserModel.class))).thenReturn(Optional.empty());
        when(userService.deleteUser(4L)).thenReturn(true);
        when(userService.deleteUser(6L)).thenReturn(false);

        mockMvc.perform(put("/api/users/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4L))
            .andExpect(jsonPath("$.name").value("Updated User"));

        mockMvc.perform(put("/api/users/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/users/4"))
            .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/users/6"))
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