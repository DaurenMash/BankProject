package com.bank.account.service;

import com.bank.account.dto.AuditDto;

import java.sql.Timestamp;
import java.util.List;

public interface AuditService {
    void logAudit(AuditDto auditDto);

    AuditDto getAuditByEntityId(Long entityIdFromCurrentAccount);

    List<AuditDto> getAllAudits();

    void deleteAllData();

    AuditDto setDataToAuditDtoForNewAudit(String entityType,
                               String operationType,
                               String createdBy,
                               String modifiedBy,
                               Timestamp createdAt,
                               Timestamp modifiedAt,
                               String newAccount,
                               String oldAccount);

    AuditDto setDataToAuditDto(AuditDto auditDto,
                           String operationType,
                           String modifiedBy,
                           Timestamp modifiedAt,
                           String newEntityJson,
                           String oldEntityJson);
}

