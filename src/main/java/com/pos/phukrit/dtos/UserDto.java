package com.pos.phukrit.dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import com.pos.phukrit.models.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    private String username;

    @NotNull
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    private String password; // Consider hashing this in the service layer

    @NotNull
    private UserRole role; // Use enum type for role
}
