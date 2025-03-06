package com.bank.account.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="account_details")
public class Account {

    @Id
    @Column(name="id")
    @NotNull
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "passport_id")
    @NotNull
    private int passportId;

    @Column(name="account_number")
    @NotNull
    private int accountNumber;

    @Column(name="bank_details_id")
    @NotNull
    private int bankDetailsId;

    @Column(name="money")
    @NotNull
    private BigDecimal money;

    @Column(name = "negative_balance")
    private boolean negativeBalance;

    @Column(name="profile_id")
    @NotNull
    private int profileId;
}
