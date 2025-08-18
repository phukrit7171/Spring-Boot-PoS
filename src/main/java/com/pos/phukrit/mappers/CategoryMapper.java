package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.CategoryDto;
import com.pos.phukrit.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);
}