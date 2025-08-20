package com.pos.phukrit.dtos;

import com.pos.phukrit.models.UserModel;
import lombok.Data;

@Data
public class UserResDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private UserModel.Role role;
}