package com.bank.antifraud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Table(name = "suspicious_card_transfer")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SuspiciousCardTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "card_transfer_id")
    int cardTransferId;

    @Column(name = "is_blocked")
    boolean blocked;

    @Column(name = "is_suspicious")
    boolean suspicious;

    @Column(name = "blocked_reason")
    String blockedReason;

    @Column(name = "suspicious_reason")
    String suspiciousReason;

    @Override
    public String toString() {
        return "SuspiciousCardTransfer{" +
                "id=" + id +
                ", cardTransferId=" + cardTransferId +
                ", blocked=" + blocked +
                ", suspicious=" + suspicious +
                ", blockedReason='" + blockedReason + '\'' +
                ", suspiciousReason='" + suspiciousReason + '\'' +
                '}';
    }
}
