package com.bank.authorization.mapper;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "modifiedAt", source = "modifiedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "createdBy", source = "createdBy", defaultExpression = "java(\"SYSTEM\")")
    @Mapping(target = "newEntityJson", source = "newEntityJson", defaultExpression = "java(\"\")")
    AuditDto toDto(Audit audit);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "modifiedAt", source = "modifiedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "createdBy", source = "createdBy", defaultExpression = "java(\"SYSTEM\")")
    @Mapping(target = "newEntityJson", source = "newEntityJson", defaultExpression = "java(\"\")")
    Audit toEntity(AuditDto auditDto);
}

