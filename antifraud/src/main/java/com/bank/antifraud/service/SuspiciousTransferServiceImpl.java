package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;

import com.bank.antifraud.mappers.SuspiciousTransferMapper;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;


import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspiciousTransferServiceImpl implements SuspiciousTransferService {
    private static final BigDecimal SUSPICIOUS_AMOUNT_THRESHOLD = new BigDecimal("10000.00");

    private final SuspiciousAccountTransferRepository accountTransferRepository;
    private final SuspiciousCardTransferRepository cardTransferRepository;
    private final SuspiciousPhoneTransferRepository phoneTransferRepository;
    private final SuspiciousTransferMapper mapper;

    @Override
    @Transactional
    public SuspiciousCardTransferDto analyzeCardTransfer(BigDecimal amount, Integer transfer_id) {
        SuspiciousCardTransferDto cardTransferDto = new SuspiciousCardTransferDto();
        if(amount.compareTo(SUSPICIOUS_AMOUNT_THRESHOLD) > 0) {
            cardTransferDto.setSuspicious(true);
            cardTransferDto.setBlocked(true);
            cardTransferDto.setSuspiciousReason("Very big amount");
            cardTransferDto.setCardTransferId(transfer_id);
            cardTransferDto.setBlockedReason("Very big amount");
        } else {
            cardTransferDto.setSuspicious(false);
            cardTransferDto.setBlocked(false);
            cardTransferDto.setSuspiciousReason("Norm");
            cardTransferDto.setCardTransferId(transfer_id);
            cardTransferDto.setBlockedReason("Norm");
        }
        cardTransferRepository.save(mapper.toEntity(cardTransferDto));
        return cardTransferDto;
    }

    @Override
    @Transactional
    public SuspiciousPhoneTransferDto analyzePhoneTransfer(BigDecimal amount, Integer transfer_id) {
        SuspiciousPhoneTransferDto phoneTransferDto = new SuspiciousPhoneTransferDto();
        if(amount.compareTo(SUSPICIOUS_AMOUNT_THRESHOLD) > 0){
            phoneTransferDto.setSuspicious(true);
            phoneTransferDto.setBlocked(true);
            phoneTransferDto.setSuspicious_reason("Very big amount");
            phoneTransferDto.setPhone_transfer_id(transfer_id);
            phoneTransferDto.setBlocked_reason("Very big amount");
        } else {
            phoneTransferDto.setSuspicious(false);
            phoneTransferDto.setBlocked(false);
            phoneTransferDto.setSuspicious_reason("norm");
            phoneTransferDto.setPhone_transfer_id(transfer_id);
            phoneTransferDto.setBlocked_reason("norm");
        }
        phoneTransferRepository.save(mapper.toEntity(phoneTransferDto));
        return phoneTransferDto;
    }

    @Override
    @Transactional
    public SuspiciousAccountTransferDto analyzeAccountTransfer(BigDecimal amount, Integer transfer_id) {
        SuspiciousAccountTransferDto accountTransferDto = new SuspiciousAccountTransferDto();
        if(amount.compareTo(SUSPICIOUS_AMOUNT_THRESHOLD) > 0){
            accountTransferDto.setSuspicious(true);
            accountTransferDto.setBlocked(true);
            accountTransferDto.setSuspicious_reason("Very big amount");
            accountTransferDto.setAccount_transfer_id(transfer_id);
            accountTransferDto.setBlocked_reason("Very big amount");
        } else {
            accountTransferDto.setSuspicious(false);
            accountTransferDto.setBlocked(false);
            accountTransferDto.setSuspicious_reason("norm");
            accountTransferDto.setAccount_transfer_id(transfer_id);
            accountTransferDto.setBlocked_reason("norm");
        }
        accountTransferRepository.save(mapper.toEntity(accountTransferDto));
        return accountTransferDto;
    }

    @Override
    @Transactional
    public SuspiciousPhoneTransferDto getPhoneTransfer(Integer id) {
        return mapper.toDTO(phoneTransferRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Phone transfer not found")));
    }

    @Override
    @Transactional
    public SuspiciousCardTransferDto getCardTransfer(Integer id) {
        return mapper.toDTO(cardTransferRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Card transfer not found")));
    }

    @Override
    @Transactional
    public SuspiciousAccountTransferDto getAccountTransfer(Integer id) {
        return mapper.toDTO(accountTransferRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Account transfer not found")));
    }

    @Override
    @Transactional
    public void deletePhoneSuspiciousTransfer(Integer id) {
        phoneTransferRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCardSuspiciousTransfer(Integer id) {
        cardTransferRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAccountSuspiciousTransfer(Integer id) {
        accountTransferRepository.deleteById(id);
    }
}
