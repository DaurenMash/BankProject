package com.bank.account.service;

import com.bank.account.dto.AuditDto;

import java.util.List;

public interface AuditService {
    void logAudit(AuditDto auditDto);

    AuditDto getAuditByEntityId(Long entityId);

    List<AuditDto> getAllAudits();

    void deleteAllData();

}
