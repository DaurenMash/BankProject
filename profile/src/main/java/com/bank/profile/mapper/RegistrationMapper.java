package com.bank.profile.mapper;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.entity.Registration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    RegistrationDto toDto(Registration entity);
    RegistrationDto toDto(ActualRegistration entity);

    Registration toRegistration(RegistrationDto dto);

    ActualRegistration toActualRegistration(RegistrationDto dto);
}
