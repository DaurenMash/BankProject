package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.License;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    LicenseDto toDto(License license);

    License toEntity(LicenseDto licenseDto);

    @Mapping(target = "bankDetails", ignore = true)
    void updateFromDto(LicenseDto licenseDto, @MappingTarget License license);

}
