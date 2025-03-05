package com.bank.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {

    private Long id;
    private String entityType;
    private String operationType;
    private String createdBy;
    private String modifiedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
    private String newEntityJson;
    private String entityJson;
}