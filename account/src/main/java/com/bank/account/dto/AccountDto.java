package com.bank.account.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private int id;
    private int passportId;
    private int accountNumber;
    private BigDecimal money;
    private boolean negativeBalance;
}
