package com.bank.publicinfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "audit", schema = "public_info")
public class Audit {

    public static final int ENTITY_TYPE_MAX_LENGTH = 40;
    public static final int OPERATION_TYPE_MAX_LENGTH = 255;
    public static final int CREATED_BY_MAX_LENGTH = 255;
    public static final int MODIFIED_BY_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = ENTITY_TYPE_MAX_LENGTH)
    @NotNull
    @Column(name = "entity_type", nullable = false, length = ENTITY_TYPE_MAX_LENGTH)
    private String entityType;

    @Size(max = OPERATION_TYPE_MAX_LENGTH)
    @NotNull
    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Size(max = CREATED_BY_MAX_LENGTH)
    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Size(max = MODIFIED_BY_MAX_LENGTH)
    @Column(name = "modified_by")
    private String modifiedBy;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "new_entity_json", columnDefinition = "jsonb")
    private String newEntityJson;

    @NotNull
    @Column(name = "entity_json", nullable = false, columnDefinition = "jsonb")
    private String entityJson;

}
