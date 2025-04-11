package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.Audit;
import com.bank.authorization.entity.EntityType;
import com.bank.authorization.entity.OperationType;
import com.bank.authorization.entity.Role;
import com.bank.authorization.mapper.AuditMapper;
import com.bank.authorization.repository.AuditRepository;
import com.bank.authorization.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_AUDIT_ID = 1L;
    private static final String SYSTEM_USER = "SYSTEM";

    @Mock
    private AuditRepository auditRepository;
    @Mock
    private AuditMapper auditMapper;
    @InjectMocks
    private AuditServiceImpl auditService;

    private UserDto givenUserDto() {
        return UserDto.builder()
                .id(TEST_USER_ID)
                .role(Role.ROLE_USER.toString())
                .build();
    }

    private Audit givenAudit(OperationType operationType) {
        return Audit.builder()
                .id(TEST_AUDIT_ID)
                .entityType(EntityType.USER.name())
                .operationType(operationType.name())
                .createdBy(SYSTEM_USER)
                .entityJson(JsonUtils.toJson(givenUserDto()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Audit givenInvalidJsonAudit() {
        return Audit.builder()
                .id(2L)
                .entityType(EntityType.USER.name())
                .operationType(OperationType.CREATE.name())
                .createdBy(SYSTEM_USER)
                .entityJson("invalid_json")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AuditDto givenAuditDto(OperationType operationType) {
        return AuditDto.builder()
                .entityType(EntityType.USER.name())
                .operationType(operationType.name())
                .createdBy(SYSTEM_USER)
                .entityJson(JsonUtils.toJson(givenUserDto()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Тесты
    @Test
    void logUserCreation_ShouldSaveCreateAudit() {
        when(auditMapper.toEntity(any(AuditDto.class)))
                .thenReturn(givenAudit(OperationType.CREATE));

        auditService.logUserCreation(givenUserDto());

        verify(auditRepository).save(argThat(audit ->
                audit.getOperationType().equals(OperationType.CREATE) &&
                        audit.getEntityType().equals(EntityType.USER)));
    }

    @Test
    void logUserUpdate_ShouldUpdateExistingAudit() {
        when(auditRepository.findAll())
                .thenReturn(List.of(givenAudit(OperationType.CREATE)));
        when(auditRepository.save(any(Audit.class)))
                .thenReturn(givenAudit(OperationType.UPDATE));

        auditService.logUserUpdate(TEST_USER_ID, givenUserDto());

        verify(auditRepository).save(argThat(audit ->
                audit.getOperationType().equals(OperationType.UPDATE)));
    }

    @Test
    void logUserUpdate_ShouldIgnoreInvalidJsonRecords() {
        when(auditRepository.findAll())
                .thenReturn(List.of(givenInvalidJsonAudit()));

        auditService.logUserUpdate(TEST_USER_ID, givenUserDto());

        verify(auditRepository, never()).save(any());
    }

    @Test
    void save_ShouldMapAndSaveAudit() {
        AuditDto dto = givenAuditDto(OperationType.UPDATE);
        when(auditMapper.toEntity(dto))
                .thenReturn(givenAudit(OperationType.UPDATE));

        auditService.save(dto);

        verify(auditRepository).save(argThat(audit ->
                audit.getOperationType().equals(OperationType.UPDATE)));
    }

    @Test
    void logUserUpdate_ShouldNotUpdateWhenUserNotFound() {
        when(auditRepository.findAll())
                .thenReturn(List.of(givenAudit(OperationType.CREATE)));

        auditService.logUserUpdate(999L, givenUserDto());

        verify(auditRepository, never()).save(any());
    }
}