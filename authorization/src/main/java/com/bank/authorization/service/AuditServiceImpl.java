package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.AuditMapper;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public AuditDto save(AuditDto auditDto) {
        if (auditDto.getCreatedBy() == null) {
            auditDto.setCreatedBy(SYSTEM_USER);
        }
        if (auditDto.getNewEntityJson() == null) {
            auditDto.setNewEntityJson("");
        }

        final Audit audit = auditMapper.toEntity(auditDto);
        final Audit savedAudit = auditRepository.save(audit);
        return auditMapper.toDto(savedAudit);
    }

    @Override
    @Transactional
    public void updateAuditForUser(Long userId, AuditDto auditDto) {
        // Поиск записи аудита по ID пользователя
        List<Audit> audits = auditRepository.findAll();
        Optional<Audit> existingAuditOpt = audits.stream()
                .filter(audit -> {
                    try {
                        User user = JsonUtils.fromJson(audit.getEntityJson(), User.class);
                        return user != null && user.getId().equals(userId);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst();

        if (existingAuditOpt.isPresent()) {
            Audit existingAudit = existingAuditOpt.get();
            existingAudit.setModifiedBy(auditDto.getModifiedBy());
            existingAudit.setModifiedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            existingAudit.setNewEntityJson(auditDto.getNewEntityJson());
            auditRepository.save(existingAudit);
        } else {
            // Если запись не найдена, создаем новую
            save(auditDto);
        }
    }
}
