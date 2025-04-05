package com.bank.publicinfo.audit;

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
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper mapper;
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

            final OperationType operationType = OperationType.UPDATE;
            final String entityType = String.valueOf(EntityType.entityTypeFromString(dto.getClass().getName()));

            final AuditDto newAuditDto = new AuditDto();
            newAuditDto.setEntityType(entityType);
            newAuditDto.setOperationType(String.valueOf(operationType));
            newAuditDto.setCreatedAt(oldAuditDto.getCreatedAt());
            newAuditDto.setCreatedBy(oldAuditDto.getCreatedBy());
            newAuditDto.setModifiedBy("SYSTEM_2"); // TODO заменить на реального пользователя
            newAuditDto.setModifiedAt(LocalDateTime.now());
            newAuditDto.setNewEntityJson(newEntityJson);
            newAuditDto.setEntityJson(oldAuditDto.getEntityJson());

            auditRepository.save(auditMapper.toEntity(newAuditDto));

            log.info("Updating success audit Log: {} - {}", entityType, newAuditDto);
        } catch (JsonProcessingException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e.getMessage());
        }
    }


    @Override
    @Transactional
    public <T> void createAudit(T dto) throws CustomJsonProcessingException {
        try {
            final String entityType = String.valueOf(EntityType.entityTypeFromString(dto.getClass().getName()));
            final OperationType operationType = OperationType.CREATE;
            final String entityJson = mapper.writeValueAsString(dto);
            final AuditDto auditDto = new AuditDto();
            auditDto.setEntityType(entityType);
            auditDto.setOperationType(String.valueOf(operationType));
            auditDto.setCreatedBy("SYSTEM"); // TODO заменить на реального пользователя
            auditDto.setModifiedBy(null);
            auditDto.setCreatedAt(LocalDateTime.now());
            auditDto.setEntityJson(entityJson);
            auditDto.setNewEntityJson(null);

            auditRepository.save(auditMapper.toEntity(auditDto));

            log.info("Creation success audit Log: {} - {}", entityType, auditDto);
        } catch (final JsonProcessingException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AuditDto> getAllAudits(Pageable pageable) {
        if (pageable == null) {
            log.error("Attempt to get all audits with null Pageable");
            final IllegalArgumentException e =
                    new IllegalArgumentException("Page parameters cannot be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        return auditRepository.findAll(pageable)
                .map(auditMapper::toDto);
    }


    @Override
    public AuditDto getAuditById(Long auditId) {
        if (auditId == null) {
            log.error("Attempt to get audit details with null ID");
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
        } catch (DataAccessException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving audit details.");
        }
    }


    @Override
    @Transactional
    public void deleteAuditById(Long auditId) {
        if (auditId == null) {
            log.error("Attempt to delete audit with null ID");
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
            throw new RuntimeException("Failed to delete audit: " + e.getMessage(), e);
        }
    }


    @Override
    public BankDetails findBankDetailsByBik(Long bik) {
        if (bik == null) {
            log.error("Attempt to find bank details with null BIK");
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
            throw e; // Пробрасываем исключение дальше
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding bank details for BIK: {}", bik, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("Failed to find bank details: " + e.getMessage(), e);
        }
    }



    @Override
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
                    throw new CustomJsonProcessingException(ERROR_PROCESSING_JSON + e.getMessage());
                }
            }
            page++;
        }
        log.info("Successfully retrieved audit for entity ID: {}", entityId);
        return resultAuditDto;
    }




}
