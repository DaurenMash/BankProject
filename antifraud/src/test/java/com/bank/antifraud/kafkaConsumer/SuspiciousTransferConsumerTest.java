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

import static com.bank.antifraud.util.SuspiciousTransferUtil.*;
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

    private static final Integer TRANSFER_ID = 1;

    @BeforeEach
    void setUp() {
        testMessage = new HashMap<>();
        testMessage.put("id", TRANSFER_ID);
    }

    @Test
    void listenCard_ShouldProcessCardTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("card_number", CARD_NUMBER.getType());
        SuspiciousCardTransferDto cardDto = new SuspiciousCardTransferDto();
        cardDto.setSuspicious(true);
        cardDto.setCardTransferId(TRANSFER_ID);

        when(transferService.analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(TRANSFER_ID))
        ).thenReturn(cardDto);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(transferService).analyzeCardTransfer(new BigDecimal(AMOUNT_HEADER.getType()), TRANSFER_ID);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(true) &&
                        response.get("transfer_id").equals(TRANSFER_ID)
        ));
    }

    @Test
    void listenCard_ShouldProcessPhoneTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("phone_number", PHONE_NUMBER);
        SuspiciousPhoneTransferDto phoneDto = new SuspiciousPhoneTransferDto();
        phoneDto.setSuspicious(false);
        phoneDto.setPhoneTransferId(TRANSFER_ID);

        when(transferService.analyzePhoneTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(TRANSFER_ID))
        ).thenReturn(phoneDto);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(transferService).analyzePhoneTransfer(new BigDecimal(AMOUNT_HEADER.getType()), TRANSFER_ID);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(false) &&
                        response.get("transfer_id").equals(TRANSFER_ID)
        ));
    }

    @Test
    void listenCard_ShouldProcessAccountTransfer() throws JsonProcessingException {
        // Arrange
        testMessage.put("account_number", "40817810099910004321");
        SuspiciousAccountTransferDto accountDto = new SuspiciousAccountTransferDto();
        accountDto.setSuspicious(true);
        accountDto.setAccountTransferId(TRANSFER_ID);

        when(transferService.analyzeAccountTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(TRANSFER_ID))
        ).thenReturn(accountDto);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(transferService).analyzeAccountTransfer(new BigDecimal(AMOUNT_HEADER.getType()), TRANSFER_ID);
        verify(kafkaProducer).eventResponse(ArgumentMatchers.argThat(response ->
                response.get("isSuspicious").equals(true) &&
                        response.get("transfer_id").equals(TRANSFER_ID)
        ));
    }

    @Test
    void listenCard_ShouldNotCallTransferService_WhenInvalidTransferId() throws JsonProcessingException {
        // Arrange
        testMessage.put("id", 0);
        testMessage.put("card_number", CARD_NUMBER);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(transferService, never()).analyzeCardTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyInt()
        );
    }

    @Test
    void listenCard_ShouldNotProduceEvent_WhenInvalidTransferId() throws JsonProcessingException {
        // Arrange
        testMessage.put("id", 0);
        testMessage.put("card_number", CARD_NUMBER);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }

    @Test
    void listenCard_ShouldHandleNullTransferId() throws JsonProcessingException {
        // Arrange
        testMessage.put("id", null);
        testMessage.put("card_number", CARD_NUMBER);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

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
        testMessage.put("account_number", ACCOUNT_NUMBER.getType());
        when(transferService.analyzeAccountTransfer(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.eq(TRANSFER_ID))
        ).thenReturn(null);

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

        // Assert
        verify(transferService).analyzeAccountTransfer(new BigDecimal(AMOUNT_HEADER.getType()), TRANSFER_ID);
        verify(kafkaProducer, never()).eventResponse(ArgumentMatchers.anyMap());
    }

    @Test
    void listenCard_ShouldHandleUnknownTransferType() throws JsonProcessingException {
        // Arrange
        testMessage.put("unknown_field", UNKNOWN_FIELD.getType());

        // Act
        listenCardForTest(testMessage, AMOUNT_HEADER.getType());

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

    private void listenCardForTest(Map<String, Object> testMessage, String head) throws JsonProcessingException {
        suspiciousTransferConsumer.listenCard(testMessage, head);
    }
}
