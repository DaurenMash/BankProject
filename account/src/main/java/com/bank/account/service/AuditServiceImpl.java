package com.bank.account.service;

import com.bank.account.aspects.AuditAspect;
import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.exception.DataAccessException;
import com.bank.account.exception.EntityNotFoundException;
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
    private final String CREATE_OPERATION = "CREATION"; //Операция создания аккаунта
    private final String UPDATE_OPERATION = "UPDATE";

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    @Transactional
    public void logAudit(AuditDto auditDto) {
        try {
            if (auditDto.getOperationType().equals(CREATE_OPERATION)) {
                Audit audit = auditRepository.save(auditMapper.toAudit(auditDto));
                log.info("Audit log successfully saved: {}", audit);
            } else if (auditDto.getOperationType().equals(UPDATE_OPERATION)) {
                Audit existingAudit = auditRepository.findAuditById(auditDto.getId());
                if (existingAudit == null) {
                    throw new EntityNotFoundException("Audit not found with ID: " + auditDto.getId());
                }

                existingAudit.setOperationType(auditDto.getOperationType());
                existingAudit.setEntityType(auditDto.getEntityType());
                existingAudit.setModifiedAt(auditDto.getModifiedAt());
                existingAudit.setCreatedAt(existingAudit.getCreatedAt());
                existingAudit.setCreatedBy(existingAudit.getCreatedBy());
                existingAudit.setModifiedBy(auditDto.getModifiedBy());
                existingAudit.setNewEntityJson(auditDto.getNewEntityJson());
                existingAudit.setEntityJson(existingAudit.getEntityJson());

                auditRepository.save(existingAudit);
                log.info("Audit log successfully updated: {}", existingAudit);
            }
        }catch (EntityNotFoundException e) {
            log.error("Audit not found: {}", e.getMessage());
            throw e;
        }  catch (DataAccessException e) {
            log.error("Database error while saving audit log: {}", e.getMessage());
            throw new DataAccessException("Failed to save audit log due to database error" + e);
        }  catch (Exception e) {
            log.error("Unexpected error while saving audit log: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while saving audit log", e);
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
                    .orElseThrow(() -> new EntityNotFoundException("Audit not found for entity ID: "
                            + entityIdFromCurrentAccount));

            log.info("Successfully retrieved audit for entity ID: {}", entityIdFromCurrentAccount);
            return resultAuditDto;
        } catch (EntityNotFoundException e) {
            log.error("Audit not found for entity ID: {}", entityIdFromCurrentAccount, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to retrieve audit for entity ID: {}", entityIdFromCurrentAccount);
            throw new RuntimeException("Unexpected error while retrieving audit", e);
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

            log.info("Successfully retrieved all audits");
            return resultAuditDtoList;
        } catch (DataAccessException e) {
            log.error("Database error while retrieving audits: {}", e.getMessage());
            throw new DataAccessException("Failed to retrieve audits due to database error: " + e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving audits: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving audits", e);
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
            if (entityType == null || entityType.isEmpty()) {
                throw new IllegalArgumentException("EntityType cannot be null or empty");
            }
            if (operationType == null || operationType.isEmpty()) {
                throw new IllegalArgumentException("OperationType cannot be null or empty");
            }
            if (createdBy == null || createdBy.isEmpty()) {
                throw new IllegalArgumentException("CreatedBy cannot be null or empty");
            }

            AuditDto auditDtoAspect = new AuditDto();
            auditDtoAspect.setEntityType(entityType);
            auditDtoAspect.setOperationType(operationType);
            auditDtoAspect.setCreatedBy(createdBy);
            auditDtoAspect.setModifiedBy(modifiedBy);
            auditDtoAspect.setCreatedAt(createdAt);
            auditDtoAspect.setModifiedAt(modifiedAt);
            auditDtoAspect.setNewEntityJson(newAccount);
            auditDtoAspect.setEntityJson(oldAccount);

            log.info("Successfully created new audit DTO for entity type: {}", entityType);
            return auditDtoAspect;
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create new audit DTO for entity type: {}", entityType, e);
            throw new RuntimeException("Unexpected error while creating audit DTO", e);
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
            if (auditDto == null) {
                throw new IllegalArgumentException("AuditDto cannot be null");
            }
            if (operationType == null || operationType.isEmpty()) {
                throw new IllegalArgumentException("OperationType cannot be null or empty");
            }
            if (newEntityJson == null || newEntityJson.isEmpty()) {
                throw new IllegalArgumentException("NewEntityJson cannot be null or empty");
            }

            Audit existingAudit = auditRepository.findAuditById(auditDto.getId());
            if (existingAudit == null) {
                throw new EntityNotFoundException("Audit not found with ID: " + auditDto.getId());
            }

            AuditDto auditDtoAspect = auditMapper.toAuditDto(existingAudit);

            auditDtoAspect.setEntityType(auditDto.getEntityType());
            auditDtoAspect.setOperationType(operationType);
            auditDtoAspect.setCreatedBy(auditDto.getCreatedBy());
            auditDtoAspect.setModifiedBy(modifiedBy);
            auditDtoAspect.setModifiedAt(modifiedAt);
            auditDtoAspect.setNewEntityJson(newEntityJson);
            auditDtoAspect.setEntityJson(oldEntityJson);
            auditDtoAspect.setCreatedAt(auditDto.getCreatedAt());

            log.info("Successfully updated audit DTO for entity ID: {}", auditDto.getId());
            return auditDtoAspect;
        } catch (EntityNotFoundException e) {
            log.error("Audit not found: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating audit DTO: {}", e.getMessage());
            throw new DataAccessException("Failed to update audit DTO due to database error: " + e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service. Failed to update audit DTO for entity ID: {}", auditDto.getId(), e);
            throw new RuntimeException("Unexpected error while updating audit DTO", e);
        }
    }
}
