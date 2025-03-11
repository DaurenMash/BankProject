package com.bank.account.service;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.mapper.AuditMapper;
import com.bank.account.repository.AuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService{
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final AccountMapper accountMapper;

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper, AccountMapper accountMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
        this.accountMapper = accountMapper;
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

    @Override
    public AuditDto setDataToAuditDtoForAspect(String entityType,
                                      String operationType,
                                      String createdBy,
                                      String modifiedBy,
                                      Timestamp createdAt,
                                      Timestamp modifiedAt,
                                      String newAccount,
                                      String oldAccount,
                                      Long entityId) {
        AuditDto auditDtoAspect = new AuditDto();
        auditDtoAspect.setEntityType(entityType);
        auditDtoAspect.setOperationType(operationType);
        auditDtoAspect.setCreatedBy(createdBy);
        auditDtoAspect.setModifiedBy(modifiedBy);
        auditDtoAspect.setCreatedAt(createdAt);
        auditDtoAspect.setModifiedAt(modifiedAt);
        auditDtoAspect.setNewEntityJson(newAccount);
        auditDtoAspect.setEntityJson(oldAccount);
        auditDtoAspect.setEntityId(entityId);
        return auditDtoAspect;
    }
}
