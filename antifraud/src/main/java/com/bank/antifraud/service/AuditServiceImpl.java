package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.globalException.DataAccessException;
import com.bank.antifraud.globalException.JsonProcessingException;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.model.Audit;
import com.bank.antifraud.repository.AuditRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final ObjectMapper objectMapper;
    private final String ENTITY_TYPE = "Anti_fraud";
    private final String CURRENT_USER = "SYSTEM";

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper, ObjectMapper objectMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public AuditDto createAudit(String operationType, String entityType,
                                String createdBy, Object newEntity,
                                Object oldEntity) {
        try {
            Audit audit = Audit.builder()
                    .entity_type(entityType)
                    .operation_type(operationType)
                    .created_by(createdBy)
                    .modified_by(null)
                    .created_at(LocalDateTime.now())
                    .modified_at(null)
                    .new_entity_json(objectMapper.writeValueAsString(newEntity))
                    .entity_json(oldEntity != null ?
                            objectMapper.writeValueAsString(oldEntity) :
                            "{}")
                    .build();

            Audit savedAudit = auditRepository.save(audit);
            return auditMapper.toDTO(savedAudit);
        } catch (Exception e) {
            log.error("Failed to create audit record: {}", e.getMessage(), e);
            throw new RuntimeException("Audit creation failed", e);
        }
    }

    @Override
    @Transactional
    public AuditDto updateAudit(int id, AuditDto auditDto) {
        Audit audit = auditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit not found with ID: " + id));
        audit.setEntity_type(auditDto.getEntity_type());
        audit.setOperation_type(auditDto.getOperation_type());
        audit.setModified_by(auditDto.getModified_by());
        audit.setModified_at(LocalDateTime.now());
        audit.setNew_entity_json(auditDto.getNew_entity_json());
        audit.setEntity_json(auditDto.getEntity_json());
        auditRepository.save(audit);
        return auditMapper.toDTO(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditDto getAuditById(Integer id) {
        return auditRepository.findById(id)
                .map(auditMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Audit not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditDto> getAllAudits() {
        try {
            // Получаем все записи аудита из базы данных
            List<Audit> audits = auditRepository.findAll();

            // Преобразуем список сущностей в список DTO
            List<AuditDto> auditDtos = audits.stream()
                    .map(auditMapper::toDTO)
                    .collect(Collectors.toList());

            log.info("Retrieved {} audit logs", auditDtos.size());
            return auditDtos;
        } catch (DataAccessException e) {
            log.error("Database error while retrieving all audit DTOs: {}", e.getMessage());
            throw new DataAccessException("Failed to retrieve all audit DTOs due to database error: " + e);
        } catch (Exception e) {
            log.error("Failed to retrieve all audit DTOs: {}", e);
            throw new RuntimeException("Unexpected error while retrieving all audit DTOs", e);
        }
    }

    @Override
    @Transactional
    public void deleteAudit(Integer id) {
        auditRepository.deleteById(id);
    }
}
