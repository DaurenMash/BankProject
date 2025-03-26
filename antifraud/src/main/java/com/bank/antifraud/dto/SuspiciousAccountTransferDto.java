package com.bank.antifraud.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SuspiciousAccountTransferDto {
    int id;

    int account_transfer_id;

    boolean blocked;

    boolean suspicious;

    @Column(nullable = false)
    String blocked_reason;

    String suspicious_reason;
}
