package com.bank.history.dto;

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

    private Long id;

    private Long transferAuditId;

    private Long profileAuditId;

    private Long accountAuditId;

    private Long antiFraudAuditId;

    private Long publicBankInfoAuditId;

    private Long authorizationAuditId;

}

