package com.bank.account.service;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.mapper.AuditMapper;
import com.bank.account.repository.AuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

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

    @Override
    @Transactional
    public AuditDto getAuditByEntityId(Long entityId) {
        return auditRepository.findAuditsByEntityId(entityId)
                .stream()
                .max(Comparator.comparing(audit ->
                        audit.getModifiedAt() != null ? audit.getModifiedAt() : audit.getCreatedAt()))
                .map(auditMapper::setDataToAuditDto)
                .orElse(null);
    }

    @Override
    public List<AuditDto> getAllAudits() {
        return auditRepository.findAll()
                .stream()
                .map(auditMapper::setDataToAuditDto)
                .toList();
    }

    @Override
    public void deleteAllData() {
        auditRepository.deleteAll();
    }
}
