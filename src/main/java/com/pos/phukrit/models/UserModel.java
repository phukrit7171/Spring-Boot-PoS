package com.pos.phukrit.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String username;
    
    private String password; // Will be hashed in the service layer
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // e.g., ADMIN, STAFF, CUSTOMER
}
