package com.bank.account.producers;

import com.bank.account.dto.AccountDto;
import org.springframework.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountProducer {
    private static final String TOPIC_AUTHORIZATION = "Authorization";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreatedAccountEvent(AccountDto accountDto, String jwtToken) {
        try {
            final Message<AccountDto> message = MessageBuilder
                    .withPayload(accountDto)
                    .setHeader(KafkaHeaders.TOPIC, "account.create")
                    .setHeader(TOPIC_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);

            log.info("Created account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account creation event {}", e.getMessage());
        }
    }

    public void sendUpdatedAccountEvent(AccountDto accountDto, String jwtToken) {
        try {
            final Message<AccountDto> message = MessageBuilder
                    .withPayload(accountDto)
                    .setHeader(KafkaHeaders.TOPIC, "account.update")
                    .setHeader(TOPIC_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);

            log.info("Updated account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account update event {}", e.getMessage());
        }
    }

    public void sendDeletedAccountEvent(AccountDto accountDto, String jwtToken) {
        try {
            final Message<AccountDto> message = MessageBuilder
                    .withPayload(accountDto)
                    .setHeader(KafkaHeaders.TOPIC, "account.delete")
                    .setHeader(TOPIC_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);

            log.info("Deleted account event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send account delete event {}", e.getMessage());
        }
    }

    public void sendGetAccountsEvent(String jwtToken) {
        try {
            final Message<String> message = MessageBuilder
                    .withPayload("")
                    .setHeader(KafkaHeaders.TOPIC, "account.get")
                    .setHeader(TOPIC_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);

            log.info("Get accounts event successfully finished");
        } catch (Exception e) {
            log.error("Failed to send accounts get event {}", e.getMessage());
        }
    }

    public void sendGetOneAccountByIdEvent(String json, String jwtToken) {
        try {
            final Message<String> message = MessageBuilder
                    .withPayload(json)
                    .setHeader(KafkaHeaders.TOPIC, "account.get.byId")
                    .setHeader(TOPIC_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);

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
