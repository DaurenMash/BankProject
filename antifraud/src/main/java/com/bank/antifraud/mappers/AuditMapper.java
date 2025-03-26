package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.model.Audit;
import com.bank.antifraud.repository.AuditRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface AuditMapper {
    AuditDto toDTO(Audit audit);
    Audit toEntity(AuditDto auditDTO);
}
