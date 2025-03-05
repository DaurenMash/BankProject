package com.bank.account.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

@Entity
@Table(name="audit")
public class Audit {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="entity_type")
    @Size(max=50)
    private String entityType;

    @Column(name="operation_type")
    @Size(max=255)
    private String operationType;

    @Column(name="created_by")
    @Size(max=255)
    private String createdBy;

    @Column(name="modified_by")
    @Size(max=255)
    private String modifiedBy;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="modified_at")
    private Timestamp modifiedAt;

    @Column(name="new_entity_json")
    private String newEntityJson;

    @Column(name="entity_json")
    private String entityJson;
}
