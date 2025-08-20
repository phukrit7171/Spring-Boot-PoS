package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class ProductReqDto {
    private String name;
    private String description;
    private double price;
    private int stock;
}