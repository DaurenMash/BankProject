package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.entity.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    CertificateDto toDto(Certificate certificate);

    Certificate toEntity(CertificateDto certificateDto);

    @Mapping(target = "bankDetails", ignore = true)
    void updateFromDto(CertificateDto certificateDto,
                       @MappingTarget Certificate certificate);

}
