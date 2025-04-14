package com.bank.transfer.aspect.aspect;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.kafka.TransferProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {
    private final TransferProducer transferProducer;

    @Before("execution(* com.bank.transfer.service.TransferServiceImpl.saveAccountTransfer(..)) && args(dto)")
    public void processAccountTransfer(AccountTransferDto dto) {
        try {
            transferProducer.sendAccountTransfer(dto);
            log.info("Account transfer initiated: accountNumber={}, amount={}", dto.getAccountNumber(), dto.getAmount());
        } catch (Exception e) {
            log.error("Failed to initiate account transfer: accountNumber={}", dto.getAccountNumber());
            throw new RuntimeException("Failed to process account transfer", e);
        }
    }

    @Before("execution(* com.bank.transfer.service.TransferServiceImpl.saveCardTransfer(..)) && args(dto)")
    public void processCardTransfer(CardTransferDto dto) {
        try {
            transferProducer.sendCardTransfer(dto);
            log.info("Card transfer initiated: cardNumber={}, amount={}", dto.getCardNumber(), dto.getAmount());
        } catch (Exception e) {
            log.error("Failed to initiate card transfer: cardNumber={}", dto.getCardNumber());
            throw new RuntimeException("Failed to process card transfer", e);
        }
    }

    @Before("execution(* com.bank.transfer.service.TransferServiceImpl.savePhoneTransfer(..)) && args(dto)")
    public void processPhoneTransfer(PhoneTransferDto dto) {
        try {
            transferProducer.sendPhoneTransfer(dto);
            log.info("Phone transfer initiated: phoneNumber={}, amount={}", dto.getPhoneNumber(), dto.getAmount());
        } catch (Exception e) {
            log.error("Failed to initiate phone transfer: phoneNumber={}", dto.getPhoneNumber());
            throw new RuntimeException("Failed to process phone transfer", e);
        }
    }
}