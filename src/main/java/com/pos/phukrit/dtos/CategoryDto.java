package com.pos.phukrit.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}