package com.pos.phukrit.dtos;

import com.pos.phukrit.models.UserModel;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Data
public class UserReqDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password; // We need the password to create the user
    
    @NotNull(message = "Role is required")
    private UserModel.Role role;
}