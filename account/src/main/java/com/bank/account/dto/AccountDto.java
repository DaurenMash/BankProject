package com.bank.account.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private Long passportId;

    @NotNull
    private Long accountNumber;

    @NotNull
    private BigDecimal money;

    @NotNull
    private boolean negativeBalance;

    @NotNull
    private Long bankDetailsId;

    @NotNull
    private Long profileId;
}
