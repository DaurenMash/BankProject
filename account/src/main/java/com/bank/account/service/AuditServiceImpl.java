package com.bank.account.service;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.mapper.AuditMapper;
import com.bank.account.repository.AuditRepository;
import com.bank.account.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
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
        try {
            if (auditDto.getOperationType().equals("createNewAccount")) {
                auditRepository.save(auditMapper.toAudit(auditDto));
            } else if (auditDto.getOperationType().equals("updateCurrentAccount")) {
                Audit existingAudit = auditRepository.findAuditById(auditDto.getId());

                existingAudit.setOperationType(auditDto.getOperationType());
                existingAudit.setEntityType(auditDto.getEntityType());
                existingAudit.setModifiedAt(auditDto.getModifiedAt());
                existingAudit.setCreatedAt(existingAudit.getCreatedAt());
                existingAudit.setCreatedBy(existingAudit.getCreatedBy());
                existingAudit.setModifiedBy(auditDto.getModifiedBy());
                existingAudit.setNewEntityJson(auditDto.getNewEntityJson());
                existingAudit.setEntityJson(existingAudit.getEntityJson());

                auditRepository.save(existingAudit);
            }

            log.info("Service. Audit log successfully saved");
        } catch (Exception e) {
            log.error("Service. Audit log failed {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditDto getAuditByEntityId(Long entityIdFromCurrentAccount) {
        try {
            List<AuditDto> auditDtoList = getAllAudits();
            AuditDto resultAuditDto = auditDtoList.stream()
                    .filter(auditDto -> {
                        try {
                            Long entityIdFromJson = JsonUtils.extractEntityIdFromJson(auditDto.getEntityJson());
                            return entityIdFromJson.equals(entityIdFromCurrentAccount);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse entityJson", e);
                        }
                    })
                    .findFirst()
                    .orElse(null);

            if (resultAuditDto != null) {
                log.info("Service. Successfully retrieved audit for entity ID: {}", entityIdFromCurrentAccount);
            } else {
                log.warn("Service. No audit found for entity ID: {}", entityIdFromCurrentAccount);
            }
            return resultAuditDto;
        } catch (Exception e) {
            log.error("Service. Failed to retrieve audit for entity ID: {}", entityIdFromCurrentAccount);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditDto> getAllAudits() {
        try {
            List<AuditDto> resultAuditDtoList = auditRepository.findAll()
                    .stream()
                    .map(auditMapper::toAuditDto)
                    .toList();

            log.info("Service. Successfully retrieved all audits");
            return resultAuditDtoList;
        } catch (Exception e) {
            log.error("Service. Failed to retrieve all audits");
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAllData() {
        try {
            auditRepository.deleteAll();

            log.info("Service. Successfully deleted all audits");
        } catch (Exception e) {
            log.error("Service. Failed to delete all audits");
        }
    }

    @Override
    public AuditDto setDataToAuditDtoForNewAudit(String entityType,
                                      String operationType,
                                      String createdBy,
                                      String modifiedBy,
                                      Timestamp createdAt,
                                      Timestamp modifiedAt,
                                      String newAccount,
                                      String oldAccount) {
        try {
            AuditDto auditDtoAspect = new AuditDto();
            auditDtoAspect.setEntityType(entityType);
            auditDtoAspect.setOperationType(operationType);
            auditDtoAspect.setCreatedBy(createdBy);
            auditDtoAspect.setModifiedBy(modifiedBy);
            auditDtoAspect.setCreatedAt(createdAt);
            auditDtoAspect.setModifiedAt(modifiedAt);
            auditDtoAspect.setNewEntityJson(newAccount);
            auditDtoAspect.setEntityJson(oldAccount);

            log.info("Service. Successfully created new audit DTO for entity type: {}", entityType);
            return auditDtoAspect;
        } catch (Exception e) {
            log.error("Service. Failed to create new audit DTO for entity type: {}", entityType, e);
            throw e;
        }
    }

    @Override
    public AuditDto setDataToAuditDto(AuditDto auditDto,
                                  String operationType,
                                  String modifiedBy,
                                  Timestamp modifiedAt,
                                  String newEntityJson,
                                  String oldEntityJson) {
        try {
            AuditDto auditDtoAspect = auditMapper.toAuditDto(auditRepository.findAuditById(auditDto.getId()));

            auditDtoAspect.setEntityType(auditDto.getEntityType());
            auditDtoAspect.setOperationType(operationType);
            auditDtoAspect.setCreatedBy(auditDto.getCreatedBy());
            auditDtoAspect.setModifiedBy(modifiedBy);
            auditDtoAspect.setModifiedAt(modifiedAt);
            auditDtoAspect.setNewEntityJson(newEntityJson);
            auditDtoAspect.setEntityJson(oldEntityJson);
            auditDtoAspect.setCreatedAt(auditDto.getCreatedAt());

            log.info("Service. Successfully updated audit DTO for entity ID: {}", auditDto.getId());
            return auditDtoAspect;
        } catch (Exception e) {
            log.error("Service. Failed to update audit DTO for entity ID: {}", auditDto.getId(), e);
            throw e;
        }
    }
}
