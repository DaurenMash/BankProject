package com.bank.transfer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CardTransferDto {
    private Long id; // Добавляем поле id
    private Long cardNumber;
    private BigDecimal amount;
    private String purpose;
    private Long accountDetailsId;
}