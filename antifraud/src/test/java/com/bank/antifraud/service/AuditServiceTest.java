package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.globalException.DataAccessException;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.model.Audit;
import com.bank.antifraud.repository.AuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AuditMapper auditMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditServiceImpl auditService;

    private Audit audit;
    private AuditDto auditDto;

    @BeforeEach
    void setUp() {
        audit = new Audit();
        audit.setId(1);
        audit.setEntityType("SuspiciousCardTransfer");
        audit.setOperationType("CREATE");
        audit.setCreatedBy("system");
        audit.setCreatedAt(LocalDateTime.now());
        audit.setNewEntityJson("{}");
        audit.setEntityJson("{}");

        auditDto = new AuditDto();
        auditDto.setId(1);
        auditDto.setEntityType("SuspiciousCardTransfer");
        auditDto.setOperationType("CREATE");
        auditDto.setCreatedBy("system");
        auditDto.setNewEntityJson("{}");
        auditDto.setEntityJson("{}");
    }

    @Test
    void createAudit_Success() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);
        when(auditMapper.toAuditDTO(any(Audit.class))).thenReturn(auditDto);

        // Act
        AuditDto result = auditService.createAudit("CREATE", "SuspiciousCardTransfer",
                "system", new Object(), null);

        // Assert
        assertNotNull(result);
        assertEquals("SuspiciousCardTransfer", result.getEntityType());
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void createAudit_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                auditService.createAudit("CREATE", "SuspiciousCardTransfer",
                        "system", new Object(), null));
    }

    @Test
    void updateAudit_Success() {
        // Arrange
        when(auditRepository.findById(1)).thenReturn(Optional.of(audit));
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);
        when(auditMapper.toAuditDTO(any(Audit.class))).thenReturn(auditDto);

        // Act
        AuditDto result = auditService.updateAudit(1, auditDto);

        // Assert
        assertNotNull(result);
        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    void updateAudit_NotFound() {
        // Arrange
        when(auditRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                auditService.updateAudit(1, auditDto));
    }

    @Test
    void getAuditById_Success() {
        // Arrange
        when(auditRepository.findById(1)).thenReturn(Optional.of(audit));
        when(auditMapper.toAuditDTO(audit)).thenReturn(auditDto);

        // Act
        AuditDto result = auditService.getAuditById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getAuditById_NotFound() {
        // Arrange
        when(auditRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                auditService.getAuditById(1));
    }

    @Test
    void getAllAudits_Success() {
        // Arrange
        when(auditRepository.findAll()).thenReturn(Collections.singletonList(audit));
        when(auditMapper.toAuditDTO(audit)).thenReturn(auditDto);

        // Act
        List<AuditDto> result = auditService.getAllAudits();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllAudits_DataAccessException() {
        // Arrange
        when(auditRepository.findAll()).thenThrow(new DataAccessException("DB error"));

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                auditService.getAllAudits());
    }

    @Test
    void deleteAudit_Success() {
        // Arrange
        doNothing().when(auditRepository).deleteById(1);

        // Act
        auditService.deleteAudit(1);

        // Assert
        verify(auditRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteAudit_Exception() {
        // Arrange
        doThrow(new RuntimeException()).when(auditRepository).deleteById(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                auditService.deleteAudit(1));
    }
}
