package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.AuditDto;
import com.bank.publicinfo.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditDto toDto(Audit audit);

    Audit toEntity(AuditDto dto);

    void updateFromDto(AuditDto dto, @MappingTarget Audit audit);

}
