package com.bank.antifraud.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AuditDto {
    int id;
    @NotNull
    String entity_type;
    @NotNull
    String operation_type;
    @NotNull
    String created_by;

    String modified_by;
    @NotNull
    LocalDateTime created_at;

    LocalDateTime modified_at;

    String new_entity_json;
    @NotNull
    String entity_json;
}
