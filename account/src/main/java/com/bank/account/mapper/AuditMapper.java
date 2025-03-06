package com.bank.account.mapper;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;

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
}
