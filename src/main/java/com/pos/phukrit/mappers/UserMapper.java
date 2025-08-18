package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.UserDto;
import com.pos.phukrit.models.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true) // Don't map password to DTO for security
    UserDto toDto(UserModel user);
    
    @Mapping(target = "password", ignore = true) // Don't map password from DTO
    UserModel toEntity(UserDto userDto);
}