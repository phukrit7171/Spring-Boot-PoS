package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class LoginReqDto {
    private String username;
    private String password;
}