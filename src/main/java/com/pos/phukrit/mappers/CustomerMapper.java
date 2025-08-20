package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.CustomerReqDto;
import com.pos.phukrit.dtos.CustomerResDto;
import com.pos.phukrit.models.CustomerModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerResDto toCustomerResDto(CustomerModel customerModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "points", ignore = true)
    CustomerModel toCustomerModel(CustomerReqDto customerReqDto);
}