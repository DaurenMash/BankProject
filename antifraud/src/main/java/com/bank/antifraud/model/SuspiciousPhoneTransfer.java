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
public class SuspiciousPhoneTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int phone_transfer_id;
    @Column(name = "is_blocked")
    boolean blocked;

    @Column(name = "is_suspicious")
    boolean suspicious;

    @Column(nullable = false)
    String blocked_reason;

    String suspicious_reason;
}
