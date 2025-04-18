package com.bank.publicinfo.service;

import com.bank.publicinfo.config.JacksonMapperConfig;
import com.bank.publicinfo.dto.AuditDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.enumtype.EntityType;
import com.bank.publicinfo.enumtype.OperationType;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.mapper.AuditMapper;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.repository.AuditRepository;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.exception.CustomJsonProcessingException;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private static final String ID_FIELD = "id";
    private static final String ERROR_PROCESSING_JSON = "Error processing JSON: ";
    private static final String AUDIT_ID_NULL_MESSAGE = "Audit ID must not be null";
    private static final String AUDIT_NOT_FOUND_MESSAGE = "Audit not found with ID: ";

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final BankDetailsRepository bankDetailsRepository;
    private final JacksonMapperConfig mapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    @Transactional
    public <T> void updateAudit(T dto) throws CustomJsonProcessingException, ValidationException {
        try {
            final String newEntityJson = mapper.writeValueAsString(dto);
            final JsonNode newEJson = mapper.readTree(newEntityJson);
            final Long newId = newEJson.get(ID_FIELD).asLong();
            final AuditDto oldAuditDto = findAuditByEntityJsonId(newId);

            if (oldAuditDto == null) {
                throw new ValidationException("Audit with entity JSON " + newEntityJson + " not found");
            }

            final AuditDto newAuditDto = buildAuditDto(
                    dto.getClass(),
                    OperationType.UPDATE,
                    oldAuditDto.getCreatedBy(),
                    "SYSTEM_2", // TODO заменить на реального пользователя
                    oldAuditDto.getCreatedAt(),
                    LocalDateTime.now(),
                    oldAuditDto.getEntityJson(),
                    newEntityJson
            );

            auditRepository.save(auditMapper.toEntity(newAuditDto));
            log.info("Audit update successful: {} - {}", newAuditDto.getEntityType(), newAuditDto);
        } catch (JsonProcessingException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e);
        }
    }

    @Override
    @Transactional
    public <T> void createAudit(T dto) throws CustomJsonProcessingException {
        try {
            final String entityJson = mapper.writeValueAsString(dto);
            final AuditDto auditDto = buildAuditDto(
                    dto.getClass(),
                    OperationType.CREATE,
                    "SYSTEM", // TODO заменить на реального пользователя
                    null,
                    LocalDateTime.now(),
                    null,
                    entityJson,
                    null
            );

            auditRepository.save(auditMapper.toEntity(auditDto));
            log.info("Audit create successful: {} - {}", auditDto.getEntityType(), auditDto);
        } catch (final JsonProcessingException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e);
        }
    }

    private <T> AuditDto buildAuditDto(
            Class<T> entityClass,
            OperationType operationType,
            String createdBy,
            String modifiedBy,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            String entityJson,
            String newEntityJson
    ) {
        final String entityType = String.valueOf(EntityType.entityTypeFromString(entityClass.getName()));
        final AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(entityType);
        auditDto.setOperationType(String.valueOf(operationType));
        auditDto.setCreatedBy(createdBy);
        auditDto.setModifiedBy(modifiedBy);
        auditDto.setCreatedAt(createdAt);
        auditDto.setModifiedAt(modifiedAt);
        auditDto.setEntityJson(entityJson);
        auditDto.setNewEntityJson(newEntityJson);
        return auditDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditDto> getAllAudits(Pageable pageable) {
        if (pageable == null) {
            final IllegalArgumentException e =
                    new IllegalArgumentException("Page parameters cannot be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        return auditRepository.findAll(pageable).map(auditMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditDto getAuditById(Long auditId) {
        if (auditId == null) {
            final IllegalArgumentException e = new IllegalArgumentException(AUDIT_ID_NULL_MESSAGE);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final AuditDto auditDto = auditRepository.findById(auditId)
                    .map(auditMapper::toDto)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException(AUDIT_NOT_FOUND_MESSAGE + auditId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved audit with ID: {}", auditId);
            return auditDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAuditById(Long auditId) {
        if (auditId == null) {
            final IllegalArgumentException e = new IllegalArgumentException(AUDIT_ID_NULL_MESSAGE);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            if (!auditRepository.existsById(auditId)) {
                final EntityNotFoundException e = new EntityNotFoundException(AUDIT_NOT_FOUND_MESSAGE + auditId);
                globalExceptionHandler.handleException(e, errorTopic);
                throw e;
            }
            auditRepository.deleteById(auditId);
            log.info("Successfully deleted audit with ID: {}", auditId);
        } catch (DataAccessException e) {
            log.error("Data access error occurred while deleting audit with ID: {}", auditId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting audit with ID: {}", auditId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BankDetails findBankDetailsByBik(Long bik) {
        if (bik == null) {
            final IllegalArgumentException e = new IllegalArgumentException("BIK must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails bankDetails = bankDetailsRepository.findByBik(bik);
            if (bankDetails == null) {
                final EntityNotFoundException e = new EntityNotFoundException("Bank details not found for BIK: " + bik);
                globalExceptionHandler.handleException(e, errorTopic);
                throw e;
            }
            log.info("Successfully retrieved bank details for BIK: {}", bik);
            return bankDetails;
        } catch (DataAccessException e) {
            log.error("Data access error occurred while finding bank details for BIK: {}", bik, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding bank details for BIK: {}", bik, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditDto findAuditByEntityJsonId(Long entityId) throws CustomJsonProcessingException {
        int page = 0;
        final int size = 10;
        boolean found = false;
        AuditDto resultAuditDto = null;

        while (!found) {
            final Pageable pageable = PageRequest.of(page, size);
            final Page<AuditDto> auditPage = getAllAudits(pageable);
            if (auditPage.isEmpty()) {
                break;
            }
            for (AuditDto auditDto : auditPage.getContent()) {
                try {
                    final JsonNode jsonNode = mapper.readTree(auditDto.getEntityJson());
                    if (jsonNode.has(ID_FIELD)) {
                        final Long id = jsonNode.get(ID_FIELD).asLong();
                        if (id.equals(entityId)) {
                            resultAuditDto = auditDto;
                            found = true;
                            break;
                        }
                    }
                } catch (JsonProcessingException e) {
                    globalExceptionHandler.handleException(e, errorTopic);
                    throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e);
                }
            }
            page++;
        }
        log.info("Successfully retrieved audit for entity ID: {}", entityId);
        return resultAuditDto;
    }
}
