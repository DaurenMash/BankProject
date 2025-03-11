package com.bank.account.service;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Account;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.List;

public interface AuditService {
    void logAudit(AuditDto auditDto);

    AuditDto getAuditByEntityId(Long entityId);

    List<AuditDto> getAllAudits();

    void deleteAllData();

    AuditDto setDataToAuditDtoForAspect(String entityType,
                               String operationType,
                               String createdBy,
                               String modifiedBy,
                               Timestamp createdAt,
                               Timestamp modifiedAt,
                               String newAccount,
                               String oldAccount,
                               Long entityId);
}

