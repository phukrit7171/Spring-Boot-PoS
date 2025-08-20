package com.pos.phukrit.dtos;

import lombok.Data;

@Data
public class ProductResDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
}