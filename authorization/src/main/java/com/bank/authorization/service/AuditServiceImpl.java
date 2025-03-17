package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.AuditMapper;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final String SYSTEM_USER = "SYSTEM";
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuditDto> getAllAudits() {
        return auditRepository.findAll().stream()
                .map(auditMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditDto> getAuditById(Long id) {
        return auditRepository.findById(id)
                .map(auditMapper::toDto);
    }

    @Override
    @Transactional
    public void save(AuditDto auditDto) {
        Audit audit = auditMapper.toEntity(auditDto);
        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void updateAuditForUser(Long userId, AuditDto auditDto) {

        List<Audit> audits = auditRepository.findAll();

        Audit existingAudit = null;

        for (Audit audit : audits) {
            try {
                User user = JsonUtils.fromJson(audit.getEntityJson(), User.class);
                if (user != null && user.getId().equals(userId)) {
                    existingAudit = audit;
                    break;
                }
            } catch (Exception e) {
                log.error("Ошибка при разборе JSON из audit.entity_json, ID записи: {}, ошибка: {}",
                        audit.getId(), e.getMessage(), e);
            }
        }
        if (existingAudit != null) {
            existingAudit.setOperationType(auditDto.getOperationType());
            existingAudit.setModifiedBy(auditDto.getModifiedBy());
            existingAudit.setModifiedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            existingAudit.setNewEntityJson(auditDto.getNewEntityJson());
            auditRepository.save(existingAudit);
        }
        else {
            log.error("Нет записи аудита для пользователя с ID: {}", userId);
        }
    }
}
