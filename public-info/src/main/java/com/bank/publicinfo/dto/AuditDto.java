package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import static com.bank.publicinfo.entity.Audit.CREATED_BY_MAX_LENGTH;
import static com.bank.publicinfo.entity.Audit.ENTITY_TYPE_MAX_LENGTH;
import static com.bank.publicinfo.entity.Audit.MODIFIED_BY_MAX_LENGTH;
import static com.bank.publicinfo.entity.Audit.OPERATION_TYPE_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDto implements Serializable {

    private Long id;

    @Size(max = ENTITY_TYPE_MAX_LENGTH)
    @NotNull
    private String entityType;

    @Size(max = OPERATION_TYPE_MAX_LENGTH)
    @NotNull
    private String operationType;

    @Size(max = CREATED_BY_MAX_LENGTH)
    @NotNull
    private String createdBy;

    @Size(max = MODIFIED_BY_MAX_LENGTH)
    private String modifiedBy;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private String newEntityJson;

    @NotNull
    private String entityJson;

}
