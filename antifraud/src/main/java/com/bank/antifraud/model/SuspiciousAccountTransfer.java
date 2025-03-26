package com.bank.antifraud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Table(name = "suspicious_account_transfers")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SuspiciousAccountTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int account_transfer_id;
    @Column(name = "is_blocked")
    boolean blocked;

    @Column(name = "is_suspicious")
    boolean suspicious;

    @Column(nullable = false)
    String blocked_reason;

    String suspicious_reason;
}
