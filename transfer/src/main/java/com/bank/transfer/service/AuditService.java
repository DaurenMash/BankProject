package com.bank.transfer.service;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.AuditDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;

import java.util.List;

public interface AuditService {
    List<AuditDto> getAuditHistory();

    void auditAccountTransfer(AccountTransferDto dto);
    void auditCardTransfer(CardTransferDto dto);
    void auditPhoneTransfer(PhoneTransferDto dto);
}