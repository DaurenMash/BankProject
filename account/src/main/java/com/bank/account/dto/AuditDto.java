package com.bank.account.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {

    @NotNull
    private int id;

    @NotNull
    private String entityType;

    @NotNull
    private String operationType;

    @NotNull
    private String createdBy;

    @NotNull
    private String modifiedBy;

    @NotNull
    private Timestamp createdAt;

    @NotNull
    private Timestamp modifiedAt;

    @NotNull
    private String newEntityJson;

    @NotNull
    private String entityJson;
}
