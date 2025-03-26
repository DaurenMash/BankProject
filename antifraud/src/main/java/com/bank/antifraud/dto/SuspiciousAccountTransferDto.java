package com.bank.antifraud.dto;

import jakarta.persistence.Column;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SuspiciousAccountTransferDto {
    int id;

    int account_transfer_id;

    boolean blocked;

    boolean suspicious;

    @Column(nullable = false)
    String blocked_reason;

    String suspicious_reason;
}
