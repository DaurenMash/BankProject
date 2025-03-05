package com.bank.authorization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit")
@Data
@NoArgsConstructor
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "entity_type", length = 40, nullable = false)
    private String entityType;

    @Column(name = "operation_type", length = 255, nullable = false)
    private String operationType;

    @Column(name = "created_by", length = 255, nullable = false)
    private String createdBy;

    @Column(name = "modified_by", length = 255)
    private String modifiedBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modified_at")
    private OffsetDateTime modifiedAt;

    @Lob
    @Column(name = "new_entity_json")
    private String newEntityJson;

    @Lob
    @Column(name = "entity_json", nullable = false)
    private String entityJson;
}