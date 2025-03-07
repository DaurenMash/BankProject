package com.bank.history.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HistoryDto {

    @NotNull
    private Long id;

    @NotNull
    private Long transferAuditId;

    @NotNull
    private Long profileAuditId;

    @NotNull
    private Long accountAuditId;

    @NotNull
    private Long antiFraudAuditId;

    @NotNull
    private Long publicBankInfoAuditId;

    @NotNull
    private Long authorizationAuditId;

}
