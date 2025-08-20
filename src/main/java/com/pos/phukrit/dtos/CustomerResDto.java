package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class CustomerResDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private int points;
}