package com.bank.profile.mapper;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.entity.AccountDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountDetailsMapper {

    @Mapping(source = "profile.id", target = "profileId")
    AccountDetailsDto toDto(AccountDetails dto);

    @Mapping(source = "profileId", target = "profile.id")
    AccountDetails toEntity(AccountDetailsDto dto);
}
