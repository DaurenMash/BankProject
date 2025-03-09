package com.bank.account.service;

import com.bank.account.dto.AuditDto;

public interface AuditService {
    public void logAudit(AuditDto auditDto);

}
