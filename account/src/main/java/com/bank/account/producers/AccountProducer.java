package com.bank.account.producers;

import com.bank.account.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreatedAccountEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("account.create", accountDto);

            log.info("Created account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account creation event {}", e.getMessage());
        }

    }

    public void sendUpdatedAccountEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("account.update", accountDto);

            log.info("Updated account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account update event {}", e.getMessage());
        }
    }

    public void sendDeletedAccountEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("account.delete", accountDto);

            log.info("Deleted account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account delete event {}", e.getMessage());
        }
    }

    public void sendGetAccountEvent() {
        try {
            kafkaTemplate.send("account.get", "GET_ALL_ACCOUNTS");

            log.info("Get account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account get event {}", e.getMessage());
        }

    }
}
