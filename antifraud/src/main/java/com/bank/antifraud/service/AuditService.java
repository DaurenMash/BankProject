package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AuditService {
    AuditDto createAudit(String operationType, String entityType,
                         String createdBy, Object newEntity,
                         Object oldEntity);
    AuditDto updateAudit(int id, AuditDto auditDto);
    AuditDto getAuditById(Integer id);
    void deleteAudit(Integer id);
    List<AuditDto> getAllAudits();
}
