package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.AuditMapper;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {
    @Mock
    private AuditRepository auditRepository;
    @Mock
    private AuditMapper auditMapper;
    @InjectMocks
    private AuditServiceImpl auditService;

    private UserDto userDto;
    private Audit audit;
    private Audit auditWithInvalidJson;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setRole("USER");

        audit = new Audit();
        audit.setId(1L);
        audit.setEntityType("User");
        audit.setOperationType("CREATE");
        audit.setCreatedBy("SYSTEM");
        audit.setEntityJson(JsonUtils.toJson(userDto));
        audit.setCreatedAt(LocalDateTime.now());

        auditWithInvalidJson = new Audit();
        auditWithInvalidJson.setId(2L);
        auditWithInvalidJson.setEntityType("User");
        auditWithInvalidJson.setOperationType("CREATE");
        auditWithInvalidJson.setCreatedBy("SYSTEM");
        auditWithInvalidJson.setEntityJson("invalid_json");
        auditWithInvalidJson.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void logUserCreation_ShouldSaveAuditRecord() {
        when(auditMapper.toEntity(any(AuditDto.class))).thenReturn(audit);
        auditService.logUserCreation(userDto);
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void logUserUpdate_ShouldNotUpdateIfAuditRecordNotFound() {
        when(auditRepository.findAll()).thenReturn(Collections.emptyList());
        auditService.logUserUpdate(1L, userDto);
        verify(auditRepository, never()).save(any(Audit.class));
    }

    @Test
    void logUserUpdate_ShouldLogErrorWhenJsonParsingFails() {
        when(auditRepository.findAll()).thenReturn(List.of(auditWithInvalidJson));
        auditService.logUserUpdate(1L, userDto);
        verify(auditRepository, never()).save(any(Audit.class));
    }

    @Test
    void logUserUpdate_ShouldHandleMultipleAuditsWithSomeInvalidJson() {
        when(auditRepository.findAll()).thenReturn(List.of(auditWithInvalidJson, audit));
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);
        auditService.logUserUpdate(1L, userDto);
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void save_ShouldCallRepositorySave() {
        when(auditMapper.toEntity(any(AuditDto.class))).thenReturn(audit);
        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType("User");
        auditDto.setOperationType("CREATE");
        auditDto.setCreatedBy("SYSTEM");
        auditDto.setEntityJson(JsonUtils.toJson(userDto));
        auditDto.setCreatedAt(LocalDateTime.now());
        auditService.save(auditDto);
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void logUserUpdate_ShouldNotUpdateIfNoMatchingAuditFound() {
        when(auditRepository.findAll()).thenReturn(List.of(audit));
        auditService.logUserUpdate(999L, userDto);
        verify(auditRepository, never()).save(any(Audit.class));
    }

    @Test
    void logUserUpdate_ShouldLogErrorWhenJsonParsingThrowsException() {
        when(auditRepository.findAll()).thenReturn(List.of(audit));
        mockStatic(JsonUtils.class);
        when(JsonUtils.fromJson(anyString(), eq(User.class))).thenThrow(new RuntimeException("Ошибка парсинга JSON"));
        auditService.logUserUpdate(1L, userDto);
        verify(auditRepository, never()).save(any(Audit.class));
    }
}