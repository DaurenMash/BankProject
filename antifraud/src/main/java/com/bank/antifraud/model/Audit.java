package com.bank.antifraud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Table(name = "audit")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
