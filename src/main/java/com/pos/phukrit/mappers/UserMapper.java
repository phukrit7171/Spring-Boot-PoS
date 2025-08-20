package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.UserReqDto;
import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.models.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "role", target = "role")
    UserResDto toUserResDto(UserModel userModel);

    @Mapping(source = "role", target = "role")
    UserModel toUserModel(UserReqDto userReqDto);
}