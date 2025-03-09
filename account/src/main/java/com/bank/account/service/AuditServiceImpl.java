package com.bank.account.service;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.mapper.AuditMapper;
import com.bank.account.repository.AuditRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService{
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    @Transactional
    public void logAudit(AuditDto auditDto) {
        Audit audit = auditMapper.setAuditDtoToAudit(auditDto);
        auditRepository.save(audit);
    }
}
