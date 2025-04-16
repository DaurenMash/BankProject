package com.bank.transfer.service;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.AccountTransfer;
import com.bank.transfer.entity.CardTransfer;
import com.bank.transfer.entity.PhoneTransfer;
import com.bank.transfer.exception.GlobalExceptionHandler;
import com.bank.transfer.kafka.TransferProducer;
import com.bank.transfer.mapper.AccountTransferMapper;
import com.bank.transfer.mapper.CardTransferMapper;
import com.bank.transfer.mapper.PhoneTransferMapper;
import com.bank.transfer.repository.AccountTransferRepository;
import com.bank.transfer.repository.CardTransferRepository;
import com.bank.transfer.repository.PhoneTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {
    private final AccountTransferRepository accountTransferRepository;
    private final CardTransferRepository cardTransferRepository;
    private final PhoneTransferRepository phoneTransferRepository;
    private final AccountTransferMapper accountTransferMapper;
    private final CardTransferMapper cardTransferMapper;
    private final PhoneTransferMapper phoneTransferMapper;
    private final GlobalExceptionHandler exceptionHandler;
    private final TransferProducer transferProducer; // Добавляем зависимость

    @Override
    @Transactional
    public void saveAccountTransfer(AccountTransferDto dto) {
        try {
            AccountTransfer transfer = accountTransferMapper.toEntity(dto);
            AccountTransfer savedTransfer = accountTransferRepository.save(transfer);
            log.info("Account transfer saved: ID={}", savedTransfer.getId());

            // Обновляем DTO с ID и отправляем в suspicious-transfers.get
            AccountTransferDto savedDto = accountTransferMapper.toDto(savedTransfer);
            transferProducer.sendAccountTransferToSuspicious(savedDto);
        } catch (Exception e) {
            log.error("Failed to save account transfer: accountNumber={}", dto.getAccountNumber());
            exceptionHandler.handleException(e, null);
            throw new RuntimeException("Failed to save account transfer", e);
        }
    }

    @Override
    @Transactional
    public void saveCardTransfer(CardTransferDto dto) {
        try {
            CardTransfer transfer = cardTransferMapper.toEntity(dto);
            CardTransfer savedTransfer = cardTransferRepository.save(transfer);
            log.info("Card transfer saved: ID={}", savedTransfer.getId());

            // Обновляем DTO с ID и отправляем в suspicious-transfers.get
            CardTransferDto savedDto = cardTransferMapper.toDto(savedTransfer);
            transferProducer.sendCardTransferToSuspicious(savedDto);
        } catch (Exception e) {
            log.error("Failed to save card transfer: cardNumber={}", dto.getCardNumber());
            exceptionHandler.handleException(e, null);
            throw new RuntimeException("Failed to save card transfer", e);
        }
    }

    @Override
    @Transactional
    public void savePhoneTransfer(PhoneTransferDto dto) {
        try {
            PhoneTransfer transfer = phoneTransferMapper.toEntity(dto);
            PhoneTransfer savedTransfer = phoneTransferRepository.save(transfer);
            log.info("Phone transfer saved: ID={}", savedTransfer.getId());

            // Обновляем DTO с ID и отправляем в suspicious-transfers.get
            PhoneTransferDto savedDto = phoneTransferMapper.toDto(savedTransfer);
            transferProducer.sendPhoneTransferToSuspicious(savedDto);
        } catch (Exception e) {
            log.error("Failed to save phone transfer: phoneNumber={}", dto.getPhoneNumber());
            exceptionHandler.handleException(e, null);
            throw new RuntimeException("Failed to save phone transfer", e);
        }
    }
}