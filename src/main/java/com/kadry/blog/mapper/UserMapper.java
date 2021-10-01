package com.kadry.blog.mapper;

import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User UserDtoToUser(UserDto userDto);
}
