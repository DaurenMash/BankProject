package com.bank.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit", schema = "profile")
public class Audit {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "audit_id_seq")
    private Long id;

    @Size(max = 40)
    @NotNull
    @Column(name = "entity_type", nullable = false, length = 40)
    private String entityType;

    @Size(max = 255)
    @NotNull
    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Size(max = 255)
    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Size(max = 255)
    @Column(name = "modified_by")
    private String modifiedBy;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "entity_json", nullable = false)
    private String entityJson;

    @Column(name = "new_entity_json")
    private String newEntityJson;
}