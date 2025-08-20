package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.ProductReqDto;
import com.pos.phukrit.dtos.ProductResDto;
import com.pos.phukrit.models.ProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Maps a ProductModel to a ProductResDto
    ProductResDto toProductResDto(ProductModel productModel);

    // Maps a ProductReqDto to a ProductModel
    @Mapping(target = "id", ignore = true)
    ProductModel toProductModel(ProductReqDto productReqDto);
}