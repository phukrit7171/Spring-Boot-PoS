package com.pos.phukrit.services;

import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        // Create a sample user that our mock repository will return
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(UserModel.Role.STAFF);
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        // 1. Arrange: Tell the mock repository what to do
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        // 2. Act: Call the method we want to test
        List<UserResDto> result = userService.getAllUsers();

        // 3. Assert: Check if the result is what we expect
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());

        // Verify that the findAll method was called exactly once
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserResDto> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<UserResDto> result = userService.getUserById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(99L);
    }
}