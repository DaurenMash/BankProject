package com.bank.account.service;

import com.bank.account.dto.AuditDto;

import java.sql.Timestamp;
import java.util.List;

public interface AuditService {

    AuditDto getAuditByEntityId(Long entityIdFromCurrentAccount);

    List<AuditDto> getAllAudits();

    AuditDto createAudit(Object result);

    AuditDto updateAudit(Object result);
}

