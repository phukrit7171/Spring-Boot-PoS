package com.pos.phukrit.dtos;

import com.pos.phukrit.models.UserModel;
import lombok.Data;

@Data
public class UserReqDto {
    private String name;
    private String username;
    private String email;
    private String password; // We need the password to create the user
    private UserModel.Role role;
}