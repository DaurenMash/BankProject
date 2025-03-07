package com.bank.history.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Entity
@Table(name = "history", schema = "history")
@Getter
@Setter
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
    @NotNull
    private Long transferAuditId;

    @Column(name = "profile_audit_id")
    @NotNull
    private Long profileAuditId;

    @Column(name = "account_audit_id")
    @NotNull
    private Long accountAuditId;

    @Column(name = "anti_fraud_audit_id")
    @NotNull
    private Long antiFraudAuditId;

    @Column(name = "public_bank_info_audit_id")
    @NotNull
    private Long publicBankInfoAuditId;

    @Column(name = "authorization_audit_id")
    @NotNull
    private Long authorizationAuditId;

}
