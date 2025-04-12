package com.bank.antifraud.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SuspiciousCardTransferDto {
    long id;

    long cardTransferId;

    boolean blocked;

    boolean suspicious;

    @Column(nullable = false)
    String blockedReason;

    String suspiciousReason;
}
