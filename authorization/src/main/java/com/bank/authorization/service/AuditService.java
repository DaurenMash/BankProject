package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.Audit;
import java.util.List;
import java.util.Optional;

public interface AuditService {
    List<AuditDto> getAllAudits();
    Optional<AuditDto> getAuditById(Long id);
    AuditDto save(AuditDto auditDto);
    void deleteById(Long id);
}