package com.bank.antifraud.dto;

import jakarta.persistence.Column;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SuspiciousCardTransferDto {
    int id;

    int cardTransferId;

    boolean blocked;

    boolean suspicious;

    @Column(nullable = false)
    String blockedReason;

    String suspiciousReason;
}
