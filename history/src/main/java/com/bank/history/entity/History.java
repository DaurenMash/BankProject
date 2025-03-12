package com.bank.history.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history", schema = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

    @Id
    @Column(name = "id")
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_audit_id")
    private Long transferAuditId;

    @Column(name = "profile_audit_id")
    private Long profileAuditId;

    @Column(name = "account_audit_id")
    private Long accountAuditId;

    @Column(name = "anti_fraud_audit_id")
    private Long antiFraudAuditId;

    @Column(name = "public_bank_info_audit_id")
    private Long publicBankInfoAuditId;

    @Column(name = "authorization_audit_id")
    private Long authorizationAuditId;

}
