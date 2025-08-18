package com.pos.phukrit.dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    // Assuming UserDto will have fields like id, name, email, etc.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    private String username;
    @NotNull
    private String password; // Consider hashing this in the service layer
    @NotNull
    private String role; // e.g., ADMIN, STAFF, CUSTOMER
}
