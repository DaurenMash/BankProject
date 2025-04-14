package com.bank.transfer.kafka;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.AuditDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends an account transfer event to the 'transfer.account' topic using a secure Kafka connection.
     * @param dto The account transfer data to send.
     */
    public void sendAccountTransfer(AccountTransferDto dto) {
        kafkaTemplate.send("transfer.account", dto);
        log.info("Sent account transfer to Kafka topic 'transfer.account': accountNumber={}", dto.getAccountNumber());
    }

    /**
     * Sends a card transfer event to the 'transfer.card' topic using a secure Kafka connection.
     * @param dto The card transfer data to send.
     */
    public void sendCardTransfer(CardTransferDto dto) {
        kafkaTemplate.send("transfer.card", dto);
        log.info("Sent card transfer to Kafka topic 'transfer.card': cardNumber={}", dto.getCardNumber());
    }

    /**
     * Sends a phone transfer event to the 'transfer.phone' topic using a secure Kafka connection.
     * @param dto The phone transfer data to send.
     */
    public void sendPhoneTransfer(PhoneTransferDto dto) {
        kafkaTemplate.send("transfer.phone", dto);
        log.info("Sent phone transfer to Kafka topic 'transfer.phone': phoneNumber={}", dto.getPhoneNumber());
    }

    /**
     * Sends an audit event to the 'audit.history' topic using a secure Kafka connection.
     * @param dto The audit data to send.
     */
    public void sendAuditHistory(AuditDto dto) {
        kafkaTemplate.send("audit.history", String.valueOf(dto.getId()), dto);
        log.info("Sent audit to Kafka topic 'audit.history': id={}", dto.getId());
    }

    /**
     * Sends an account transfer to the 'suspicious-transfers.get' topic after saving to DB.
     * @param dto The account transfer data with ID.
     */
    public void sendAccountTransferToSuspicious(AccountTransferDto dto) {
        kafkaTemplate.send("suspicious-transfers.create", String.valueOf(dto.getId()), dto);
        log.info("Sent account transfer to Kafka topic 'suspicious-transfers.get': id={}", dto.getId());
    }

    /**
     * Sends a card transfer to the 'suspicious-transfers.get' topic after saving to DB.
     * @param dto The card transfer data with ID.
     */
    public void sendCardTransferToSuspicious(CardTransferDto dto) {
        kafkaTemplate.send("suspicious-transfers.create", String.valueOf(dto.getId()), dto);
        log.info("Sent card transfer to Kafka topic 'suspicious-transfers.get': id={}", dto.getId());
    }

    /**
     * Sends a phone transfer to the 'suspicious-transfers.get' topic after saving to DB.
     * @param dto The phone transfer data with ID.
     */
    public void sendPhoneTransferToSuspicious(PhoneTransferDto dto) {
        kafkaTemplate.send("suspicious-transfers.create", String.valueOf(dto.getId()), dto);
        log.info("Sent phone transfer to Kafka topic 'suspicious-transfers.get': id={}", dto.getId());
    }
}