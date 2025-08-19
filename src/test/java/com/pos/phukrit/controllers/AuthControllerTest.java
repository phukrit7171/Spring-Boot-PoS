package com.pos.phukrit.controllers;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.phukrit.config.CustomUserDetailsService;
import com.pos.phukrit.config.SecurityConfig;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.models.UserRole;
import com.pos.phukrit.services.UserService;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        ProductController.class,
        CategoryController.class,
        OrderController.class,
        UserController.class
    })
)
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private ProductController productController;
    @MockBean
    private CategoryController categoryController;
    @MockBean
    private OrderController orderController;
    @MockBean
    private UserController userController;

    

    @Test
    void login_success_returnsUserAndAuthorities() throws Exception {
        User principal = new User("john", "x", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        AuthController.LoginRequest req = new AuthController.LoginRequest("john", "pass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.username").value("john"))
            .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_ADMIN"));
    }

    @Test
    void login_failure_returns401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        AuthController.LoginRequest req = new AuthController.LoginRequest("john", "wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void register_returnsCreatedUser() throws Exception {
        UserModel user = new UserModel();
        // Use reflection to set the fields since Lombok getters/setters might not be recognized by the IDE
        setField(user, "id", 1L);
        setField(user, "name", "New User");
        setField(user, "username", "newuser");
        setField(user, "email", "newuser@example.com");
        setField(user, "password", "secret");
        setField(user, "role", UserRole.CUSTOMER);
        when(userService.createUser(any(UserModel.class))).thenReturn(user);
    
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("newuser"));
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
