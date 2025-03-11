package com.bank.account.producers;

import com.bank.account.dto.AccountDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreatedAccountEvent(AccountDto accountDto) {
        kafkaTemplate.send("account.create", accountDto);
    }

    public void sendUpdatedAccountEvent(AccountDto accountDto) {
        kafkaTemplate.send("account.update", accountDto);
    }

    public void sendDeletedAccountEvent(AccountDto accountDto) {
        kafkaTemplate.send("account.delete", accountDto);
    }

    public void sendGetAccountEvent() {
        kafkaTemplate.send("account.get", "GET_ALL_ACCOUNTS");
    }
}
