package com.bank.antifraud.model;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "suspicious_phone_transfers")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SuspiciousPhoneTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "phone_transfer_id")
    int phoneTransferId;

    @Column(name = "is_blocked")
    boolean blocked;

    @Column(name = "is_suspicious")
    boolean suspicious;

    @Column(name = "blocked_reason")
    String blockedReason;

    @Column(name = "suspicious_reason")
    String suspiciousReason;
}
