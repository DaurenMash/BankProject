package com.bank.antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Table(name = "suspicious_account_transfers")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SuspiciousAccountTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "account_transfer_id")
    long accountTransferId;

    @Column(name = "is_blocked")
    boolean blocked;

    @Column(name = "is_suspicious")
    boolean suspicious;

    @Column(name = "blocked_reason")
    String blockedReason;

    @Column(name = "suspicious_reason")
    String suspiciousReason;
}
