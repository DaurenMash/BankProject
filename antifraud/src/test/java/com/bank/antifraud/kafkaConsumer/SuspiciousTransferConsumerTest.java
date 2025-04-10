package com.bank.antifraud.kafkaConsumer;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.kafkaProducer.SuspiciousTransferProducer;
import com.bank.antifraud.service.SuspiciousTransferServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuspiciousTransferConsumerTest {

    @Mock
    private SuspiciousTransferServiceImpl transferService;

    @Mock
    private SuspiciousTransferProducer kafkaProducer;

    @InjectMocks
    private SuspiciousTransferConsumer suspiciousTransferConsumer;

    private Map<String, Object> testMessage;
    private final String amountHeader = "10000.00";
    private final Integer transferId = 1;

    @BeforeEach
    void setUp() {
        testMessage = new HashMap<>();
        testMessage.put("id", transferId);
    }

    @Test
    void listenCard_ShouldProcessCardTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("card_number", "1234567890123456");
        SuspiciousCardTransferDto cardDto = new SuspiciousCardTransferDto();
        cardDto.setSuspicious(true);
        cardDto.setCardTransferId(transferId);

        when(transferService.analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(transferId))
        ).thenReturn(cardDto);

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService).analyzeCardTransfer(new BigDecimal(amountHeader), transferId);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(true) &&
                        response.get("transfer_id").equals(transferId)
        ));
    }

    @Test
    void listenCard_ShouldProcessPhoneTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("phone_number", "+79998887766");
        SuspiciousPhoneTransferDto phoneDto = new SuspiciousPhoneTransferDto();
        phoneDto.setSuspicious(false);
        phoneDto.setPhoneTransferId(transferId);

        when(transferService.analyzePhoneTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(transferId))
        ).thenReturn(phoneDto);

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService).analyzePhoneTransfer(new BigDecimal(amountHeader), transferId);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(false) &&
                        response.get("transfer_id").equals(transferId)
        ));
    }

    @Test
    void listenCard_ShouldProcessAccountTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("account_number", "40817810099910004321");
        SuspiciousAccountTransferDto accountDto = new SuspiciousAccountTransferDto();
        accountDto.setSuspicious(true);
        accountDto.setAccountTransferId(transferId);

        when(transferService.analyzeAccountTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(transferId))
        ).thenReturn(accountDto);

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService).analyzeAccountTransfer(new BigDecimal(amountHeader), transferId);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(true) &&
                        response.get("transfer_id").equals(transferId)
        ));
    }

    @Test
    void listenCard_ShouldHandleInvalidTransferId() throws  JsonProcessingException {
        // Arrange
        testMessage.put("id", 0);
        testMessage.put("card_number", "1234567890123456");

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService, never()).analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }

    @Test
    void listenCard_ShouldHandleNullTransferId() throws JsonProcessingException {
        // Arrange
        testMessage.put("id", null);
        testMessage.put("card_number", "1234567890123456");

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService, never()).analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }

    @Test
    void listenCard_ShouldHandleServiceReturnsNull() throws JsonProcessingException {
        // Arrange
        testMessage.put("account_number", "40817810099910004321");
        when(transferService.analyzeAccountTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(transferId))
        ).thenReturn(null);

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService).analyzeAccountTransfer(new BigDecimal(amountHeader), transferId);
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }

    @Test
    void listenCard_ShouldHandleUnknownTransferType() throws JsonProcessingException {
        // Arrange
        testMessage.put("unknown_field", "some_value");

        // Act
        suspiciousTransferConsumer.listenCard(testMessage, amountHeader);

        // Assert
        verify(transferService, never()).analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
        verify(transferService, never()).analyzePhoneTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
        verify(transferService, never()).analyzeAccountTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }
}
