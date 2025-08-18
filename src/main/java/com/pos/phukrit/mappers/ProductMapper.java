package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.ProductDto;
import com.pos.phukrit.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);
    
    @Mapping(target = "category.id", source = "categoryId")
    Product toEntity(ProductDto productDto);
}