package com.bank.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    @NotNull
    @Positive
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("passportId")
    private Long passportId;

    @NotNull
    @JsonProperty("accountNumber")
    private Long accountNumber;

    @NotNull
    @JsonProperty("money")
    private BigDecimal money;

    @JsonProperty("negativeBalance")
    private boolean negativeBalance;

    @NotNull
    @JsonProperty("bankDetailsId")
    private Long bankDetailsId;

    @NotNull
    @JsonProperty("profileId")
    private Long profileId;
}
