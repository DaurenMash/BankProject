package com.bank.account.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("entityType")
    private String entityType;

    @NotNull
    @JsonProperty("operationType")
    private String operationType;

    @JsonProperty("createdBy")
    private String createdBy;

    @NotNull
    @JsonProperty("modifiedBy")
    private String modifiedBy;

    @JsonProperty("createdAt")
    private Timestamp createdAt;

    @NotNull
    @JsonProperty("modifiedAt")
    private Timestamp modifiedAt;

    @NotNull
    @JsonProperty("newEntityJson")
    private String newEntityJson;

    @NotNull
    @JsonProperty("entityJson")
    private String entityJson;
}
