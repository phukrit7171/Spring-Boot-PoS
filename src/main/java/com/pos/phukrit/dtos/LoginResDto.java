package com.pos.phukrit.dtos;

import com.pos.phukrit.models.UserModel;
import lombok.Data;

@Data
public class LoginResDto {
    private Long id;
    private String username;
    private UserModel.Role role;
}