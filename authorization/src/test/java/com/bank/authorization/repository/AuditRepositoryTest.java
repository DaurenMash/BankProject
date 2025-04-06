package com.bank.authorization.repository;

import com.bank.authorization.entity.Audit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRepositoryTest {

    @Mock
    private AuditRepository auditRepository;

    private Audit audit;

    @BeforeEach
    void setUp() {
        audit = new Audit();
        audit.setId(1L);
        audit.setEntityType("User");
        audit.setCreatedAt(LocalDateTime.parse("2025-03-24T14:11:56"));
        audit.setOperationType("CREATE");
        audit.setCreatedBy("admin");
        audit.setModifiedBy("system");
        audit.setEntityJson("{}");
        audit.setNewEntityJson("{}");
    }

    @Test
    void testSaveAudit() {
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);

        Audit savedAudit = auditRepository.save(audit);

        assertNotNull(savedAudit);
        assertEquals("admin", savedAudit.getCreatedBy());

        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    void testFindById() {
        when(auditRepository.findById(1L)).thenReturn(Optional.of(audit));

        Optional<Audit> foundAudit = auditRepository.findById(1L);

        assertTrue(foundAudit.isPresent());
        assertEquals("User", foundAudit.get().getEntityType());

        verify(auditRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateAudit() {
        audit.setOperationType("UPDATE");
        audit.setModifiedBy("system2");

        when(auditRepository.save(any(Audit.class))).thenReturn(audit);

        Audit updatedAudit = auditRepository.save(audit);

        assertEquals("UPDATE", updatedAudit.getOperationType());
        assertEquals("system2", updatedAudit.getModifiedBy());

        verify(auditRepository, times(1)).save(audit);
    }
}
