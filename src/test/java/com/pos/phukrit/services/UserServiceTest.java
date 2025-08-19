package com.pos.phukrit.services;

import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.models.UserRole;
import com.pos.phukrit.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_setsDefaultRoleAndEncodesPassword_andSaves() {
        UserModel toCreate = new UserModel();
        toCreate.setUsername("john");
        toCreate.setName("John");
        toCreate.setPassword("plain");
        toCreate.setRole(null); // ensure defaulting

        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userRepository.save(any(UserModel.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel saved = userService.createUser(toCreate);

        assertThat(saved.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(saved.getPassword()).isEqualTo("hashed");

        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("john");
    }

    @Test
    void updateUser_updatesFields_andEncodesPasswordOnlyIfProvided() {
        UserModel existing = new UserModel();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setName("Old");
        existing.setPassword("old-pass");
        existing.setRole(UserRole.CUSTOMER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(UserModel.class))).thenAnswer(inv -> inv.getArgument(0));

        // Case 1: no password provided -> keep old password
        UserModel details1 = new UserModel();
        details1.setUsername("new");
        details1.setName("New");
        details1.setPassword("");
        details1.setRole(null); // role not provided -> keep existing

        Optional<UserModel> updated1 = userService.updateUser(1L, details1);
        assertThat(updated1).isPresent();
        assertThat(updated1.get().getPassword()).isEqualTo("old-pass");
        assertThat(updated1.get().getRole()).isEqualTo(UserRole.CUSTOMER);

        // Case 2: password provided -> encode and set; role provided -> set
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        UserModel details2 = new UserModel();
        details2.setUsername("new2");
        details2.setName("New2");
        details2.setPassword("secret");
        details2.setRole(UserRole.ADMIN);

        Optional<UserModel> updated2 = userService.updateUser(1L, details2);
        assertThat(updated2).isPresent();
        assertThat(updated2.get().getPassword()).isEqualTo("hashed-secret");
        assertThat(updated2.get().getRole()).isEqualTo(UserRole.ADMIN);

        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(2)).save(any(UserModel.class));
    }

    @Test
    void deleteUser_returnsTrueIfFound_elseFalse() {
        UserModel existing = new UserModel();
        existing.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThat(userService.deleteUser(2L)).isTrue();
        assertThat(userService.deleteUser(3L)).isFalse();

        verify(userRepository).delete(existing);
    }

    @Test
    void getAllAndFindAndById_delegateToRepository() {
        when(userRepository.findAll()).thenReturn(List.of(new UserModel(), new UserModel()));
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserModel()));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(new UserModel()));

        assertThat(userService.getAllUsers()).hasSize(2);
        assertThat(userService.getUserById(5L)).isPresent();
        assertThat(userService.findByUsername("alice")).isPresent();
    }
}
