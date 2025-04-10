package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.model.Audit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditMapperTest {

    private final AuditMapper mapper = Mappers.getMapper(AuditMapper.class);

    @Test
    void toAuditDTO_shouldMapAllFieldsCorrectly() {
        // Arrange
        Audit audit = new Audit();
        audit.setId(1L);
        audit.setEntityType("SuspiciousTransfer");
        audit.setOperationType("CREATE");
        audit.setCreatedBy("admin");
        audit.setModifiedBy("system");
        audit.setCreatedAt(LocalDateTime.now());
        audit.setModifiedAt(LocalDateTime.now().plusHours(1));
        audit.setNewEntityJson("{\"id\":1}");
        audit.setEntityJson("{\"id\":1,\"status\":\"ACTIVE\"}");

        // Act
        AuditDto dto = mapper.toAuditDTO(audit);

        // Assert
        assertNotNull(dto);
        assertEquals(audit.getId(), dto.getId());
        assertEquals(audit.getEntityType(), dto.getEntityType());
        assertEquals(audit.getOperationType(), dto.getOperationType());
        assertEquals(audit.getCreatedBy(), dto.getCreatedBy());
        assertEquals(audit.getModifiedBy(), dto.getModifiedBy());
        assertEquals(audit.getCreatedAt(), dto.getCreatedAt());
        assertEquals(audit.getModifiedAt(), dto.getModifiedAt());
        assertEquals(audit.getNewEntityJson(), dto.getNewEntityJson());
        assertEquals(audit.getEntityJson(), dto.getEntityJson());
    }

    @Test
    void toAuditDTO_shouldHandleNullValues() {
        // Arrange
        Audit audit = new Audit();

        // Act
        AuditDto dto = mapper.toAuditDTO(audit);

        // Assert
        assertNotNull(dto);
        assertEquals(0L, dto.getId());
        assertNull(dto.getEntityType());
        assertNull(dto.getOperationType());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getModifiedBy());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getModifiedAt());
        assertNull(dto.getNewEntityJson());
        assertNull(dto.getEntityJson());
    }

    @Test
    void toAuditEntity_shouldMapAllFieldsCorrectly() {
        // Arrange
        AuditDto dto = new AuditDto();
        dto.setId(2L);
        dto.setEntityType("Account");
        dto.setOperationType("UPDATE");
        dto.setCreatedBy("user");
        dto.setModifiedBy("admin");
        dto.setCreatedAt(LocalDateTime.now().minusDays(1));
        dto.setModifiedAt(LocalDateTime.now());
        dto.setNewEntityJson("{\"balance\":1000}");
        dto.setEntityJson("{\"balance\":1500}");

        // Act
        Audit entity = mapper.toAuditEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getEntityType(), entity.getEntityType());
        assertEquals(dto.getOperationType(), entity.getOperationType());
        assertEquals(dto.getCreatedBy(), entity.getCreatedBy());
        assertEquals(dto.getModifiedBy(), entity.getModifiedBy());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getModifiedAt(), entity.getModifiedAt());
        assertEquals(dto.getNewEntityJson(), entity.getNewEntityJson());
        assertEquals(dto.getEntityJson(), entity.getEntityJson());
    }

    @Test
    void toAuditEntity_shouldHandleNullValues() {
        // Arrange
        AuditDto dto = new AuditDto();

        // Act
        Audit entity = mapper.toAuditEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(0L, dto.getId());
        assertNull(entity.getEntityType());
        assertNull(entity.getOperationType());
        assertNull(entity.getCreatedBy());
        assertNull(entity.getModifiedBy());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getModifiedAt());
        assertNull(entity.getNewEntityJson());
        assertNull(entity.getEntityJson());
    }

    @Test
    void bidirectionalMapping_shouldWorkCorrectly() {
        // Arrange
        Audit originalEntity = new Audit();
        originalEntity.setId(3L);
        originalEntity.setEntityType("Card");
        originalEntity.setOperationType("DELETE");
        originalEntity.setCreatedBy("system");
        originalEntity.setModifiedBy(null);
        originalEntity.setCreatedAt(LocalDateTime.now().minusHours(3));
        originalEntity.setModifiedAt(null);
        originalEntity.setNewEntityJson(null);
        originalEntity.setEntityJson("{\"id\":3}");

        // Act
        AuditDto dto = mapper.toAuditDTO(originalEntity);
        Audit mappedEntity = mapper.toAuditEntity(dto);

        // Assert
        assertNotNull(dto);
        assertNotNull(mappedEntity);
        assertEquals(originalEntity.getId(), mappedEntity.getId());
        assertEquals(originalEntity.getEntityType(), mappedEntity.getEntityType());
        assertEquals(originalEntity.getOperationType(), mappedEntity.getOperationType());
        assertEquals(originalEntity.getCreatedBy(), mappedEntity.getCreatedBy());
        assertEquals(originalEntity.getModifiedBy(), mappedEntity.getModifiedBy());
        assertEquals(originalEntity.getCreatedAt(), mappedEntity.getCreatedAt());
        assertEquals(originalEntity.getModifiedAt(), mappedEntity.getModifiedAt());
        assertEquals(originalEntity.getNewEntityJson(), mappedEntity.getNewEntityJson());
        assertEquals(originalEntity.getEntityJson(), mappedEntity.getEntityJson());
    }
}
