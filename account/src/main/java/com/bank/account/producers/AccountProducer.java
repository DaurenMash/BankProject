package com.bank.account.producers;

import com.bank.account.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void sendGetAccountsEvent(List<AccountDto> accountDtoList) {
        try {
            kafkaTemplate.send("account.get", accountDtoList);

            log.info("Get account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account get event {}", e.getMessage());
        }
    }

    public void sendGetOneAccountByIdEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("account.get.byId", accountDto);

            log.info("Get account by ID event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account get by ID event {}", e.getMessage());
        }
    }


    public void sendCreatedAccountExternalEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("external.account.create", accountDto);

            log.info("External Event. Created account event successfully finished");
        } catch (Exception e) {
            log.error("External Event. Failed to send account creation event {}", e.getMessage());
        }

    }

    public void sendUpdatedAccountExternalEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("external.account.update", accountDto);

            log.info("External Event. Updated account event successfully finished");
        } catch (Exception e) {
            log.error("External Event. Failed to send account update event {}", e.getMessage());
        }
    }

    public void sendDeletedAccountExternalEvent(String responseDelete) {
        try {
            kafkaTemplate.send("external.account.delete", responseDelete);

            log.info("External Event. Deleted account event successfully finished");
        } catch (Exception e) {
            log.error("External Event. Failed to send account delete event {}", e.getMessage());
        }
    }

    public void sendGetAccountsExternalEvent(List<AccountDto> accountDtoList) {
        try {
            kafkaTemplate.send("external.account.get", accountDtoList);

            log.info("External Event. Get account event successfully finished");
        } catch (Exception e) {
            log.error("External Event. Failed to send account get event {}", e.getMessage());
        }
    }

    public void sendGetOneAccountByIdExternalEvent(AccountDto accountDto) {
        try {
            kafkaTemplate.send("external.account.getById", accountDto);

            log.info("External Event. Get account by ID event successfully finished");
        } catch (Exception e) {
            log.error("External Event. Failed to send account get by ID event {}", e.getMessage());
        }
    }
}
