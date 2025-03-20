package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Audit;
import com.bank.account.exception.DataAccessException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.exception.JsonProcessingException;
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
    private final String ENTITY_TYPE = "Account";
    private final String CURRENT_USER = "SYSTEM";
    private final String CREATION_OPERATION = "CREATION";
    private final String UPDATING_OPERATION = "UPDATING";

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    @Transactional
    public AuditDto createAudit(Object result) {
        AccountDto accountDto = (AccountDto) result;
        try {
            String entityJson = JsonUtils.convertToJson(accountDto);

            AuditDto auditDto = new AuditDto();
            auditDto.setEntityType(ENTITY_TYPE);
            auditDto.setOperationType(CREATION_OPERATION);
            auditDto.setCreatedBy(CURRENT_USER);
            auditDto.setModifiedBy("");
            auditDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            auditDto.setModifiedAt(null);
            auditDto.setNewEntityJson(null);
            auditDto.setEntityJson(entityJson);
            Audit audit = auditRepository.save(auditMapper.toAudit(auditDto));

            log.info("Audit log successfully saved: {}", audit);
            return auditDto;
        } catch (JsonProcessingException e) {
            log.error("JSON conversion error while creating audit DTO: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating audit DTO: {}", e.getMessage());
            throw new DataAccessException("Failed to create audit DTO due to database error: " + e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data in creating method:", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to create audit DTO for entity ID: {}", accountDto.getId(), e);
            throw new RuntimeException("Unexpected error while updating audit DTO:", e);
        }
    }

    @Override
    @Transactional
    public AuditDto updateAudit(Object result) {
        AccountDto accountDto = (AccountDto) result;
        try {
            String newEntityJson = JsonUtils.convertToJson(accountDto);
            Long accountId = accountDto.getId();

            String oldEntityJson;
            AuditDto oldAuditDto = getAuditByEntityId(accountId);
            if (oldAuditDto.getNewEntityJson() == null) {
                oldEntityJson = oldAuditDto.getEntityJson();
            } else {
                oldEntityJson = oldAuditDto.getNewEntityJson();
            }
            AuditDto auditDto = new AuditDto();
            auditDto.setEntityType(oldAuditDto.getEntityType());
            auditDto.setOperationType(UPDATING_OPERATION);
            auditDto.setCreatedBy(oldAuditDto.getCreatedBy());
            auditDto.setModifiedBy(CURRENT_USER);
            auditDto.setCreatedAt(oldAuditDto.getCreatedAt());
            auditDto.setModifiedAt(new Timestamp(System.currentTimeMillis()));
            auditDto.setNewEntityJson(newEntityJson);
            auditDto.setEntityJson(oldEntityJson);

            auditRepository.save(auditMapper.toAudit(auditDto));
            log.info("Audit log successfully updated: {}", auditDto);
            return auditDto;
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
            log.error("Service. Failed to update audit DTO for entity ID: {}", accountDto.getId(), e);
            throw new RuntimeException("Unexpected error while updating audit DTO", e);
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
                        } catch (JsonProcessingException e) {
                            throw new JsonProcessingException("Failed to parse entityJson", e);
                        }
                    })
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Audit not found for entity ID: "
                            + entityIdFromCurrentAccount));

            log.info("Successfully retrieved audit for entity ID: {}", entityIdFromCurrentAccount);
            return resultAuditDto;
        } catch (JsonProcessingException e) {
            log.error("JSON conversion error while parsing auditDto: {}", e.getMessage());
            throw e;
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
}
