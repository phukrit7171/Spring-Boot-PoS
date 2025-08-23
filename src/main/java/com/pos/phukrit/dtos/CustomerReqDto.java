package com.pos.phukrit.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CustomerReqDto {
    @NotBlank(message = "Customer name is required")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}