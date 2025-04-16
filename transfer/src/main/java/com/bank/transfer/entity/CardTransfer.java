package com.bank.transfer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "card_transfer", schema = "transfer")
@Data
public class CardTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private Long cardNumber;

    @Column(name = "amount", precision = 20, scale = 2)
    private BigDecimal amount;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "account_details_id")
    private Long accountDetailsId;
}
