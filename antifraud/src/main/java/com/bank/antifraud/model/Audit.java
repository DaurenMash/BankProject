package com.bank.antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Table(name = "audit")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotNull
    @Column(name = "entity_type")
    String entityType;

    @NotNull
    @Column(name = "operation_type")
    String operationType;

    @NotNull
    @Column(name = "created_by")
    String createdBy;

    @Column(name = "modified_by")
    String modifiedBy;

    @NotNull
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "modified_at")
    LocalDateTime modifiedAt;

    @Column(name = "new_entity_json")
    String newEntityJson;

    @NotNull
    @Column(name = "entity_json")
    String entityJson;
}
