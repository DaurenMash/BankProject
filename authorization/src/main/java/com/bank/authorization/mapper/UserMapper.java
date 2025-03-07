package com.bank.authorization.mapper;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "profileId", source = "profileId")
    UserDto toDto(User user);

    @Mapping(target = "profileId", source = "profileId")
    User toEntity(UserDto userDto);

    @Mapping(target = "id", ignore = true) // Игнорируем поле id
    void updateEntityFromDto(UserDto userDto, @MappingTarget User existingUser);
}
