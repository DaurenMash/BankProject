package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import java.util.List;
import java.util.Optional;

public interface AuditService {
    List<AuditDto> getAllAudits();
    Optional<AuditDto> getAuditById(Long id);
    void save(AuditDto auditDto);
    void updateAuditForUser(Long userId, AuditDto auditDto);
}
