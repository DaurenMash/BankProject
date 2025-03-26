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
public class SuspiciousCardTransferDto {
    int id;

    int cardTransferId;

    boolean blocked;

    boolean suspicious;

    @Column(nullable = false)
    String blockedReason;

    String suspiciousReason;

    @Override
    public String toString() {
        return "SuspiciousCardTransferDto{" +
                "id=" + id +
                ", cardTransferId=" + cardTransferId +
                ", blocked=" + blocked +
                ", suspicious=" + suspicious +
                ", blockedReason='" + blockedReason + '\'' +
                ", suspiciousReason='" + suspiciousReason + '\'' +
                '}';
    }
}
