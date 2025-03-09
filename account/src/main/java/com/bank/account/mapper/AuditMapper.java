package com.bank.account.mapper;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class AuditMapper {
    public AuditDto setDataToAuditDto(Audit audit){
        AuditDto auditDto = new AuditDto();
        auditDto.setId(audit.getId());
        auditDto.setCreatedAt(audit.getCreatedAt());
        auditDto.setCreatedBy(audit.getCreatedBy());
        auditDto.setModifiedAt(audit.getModifiedAt());
        auditDto.setModifiedBy(audit.getModifiedBy());
        auditDto.setEntityType(audit.getEntityType());
        auditDto.setOperationType(audit.getOperationType());
        auditDto.setNewEntityJson(audit.getNewEntityJson());
        auditDto.setEntityJson(audit.getEntityJson());
        return auditDto;
    }

    public Audit setAuditDtoToAudit(AuditDto auditDto){
        Audit audit = new Audit();
        audit.setId(auditDto.getId());
        audit.setCreatedAt(auditDto.getCreatedAt());
        audit.setCreatedBy(auditDto.getCreatedBy());
        audit.setModifiedAt(auditDto.getModifiedAt());
        audit.setModifiedBy(auditDto.getModifiedBy());
        audit.setEntityType(auditDto.getEntityType());
        audit.setOperationType(auditDto.getOperationType());
        audit.setNewEntityJson(auditDto.getNewEntityJson());
        audit.setEntityJson(auditDto.getEntityJson());
        return audit;
    }
}
