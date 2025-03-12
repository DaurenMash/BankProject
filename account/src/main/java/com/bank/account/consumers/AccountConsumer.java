package com.bank.account.consumers;

import com.bank.account.dto.AccountDto;
import com.bank.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountService accountService;

    public AccountConsumer(AccountService accountService) {
        this.accountService = accountService;
    }

    @KafkaListener (topics = "account.create", groupId = "account-group")
    public void handleCreateAccount(@Payload String json) {
        try {
            AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            accountService.createNewAccount(accountDto);

            log.info("Method 'handleCreateAccount' completed successful");
        } catch (Exception e) {
            log.error("Method 'handleCreateAccount' failed {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.update", groupId = "account-group")
    public void handleUpdateAccount(@Payload String json) {
        try {
            AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            accountService.updateCurrentAccount(accountDto.getId(), accountDto);

            log.info("Method 'handleUpdateAccount' completed  successful ");
        } catch (Exception e) {
            log.error("Method 'handleUpdateAccount' failed {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.delete", groupId = "account-group")
    public void handleDeleteAccount(@Payload String json) {
        try {
            AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            accountService.deleteAccount(accountDto);

            log.info("Method 'handleDeleteAccount' completed successful ");
        } catch (Exception e) {
            log.error("Method 'handleDeleteAccount' failed {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.get", groupId = "account-group")
    public List<AccountDto> handleGetAccounts() {
        try {
            List<AccountDto> accountsDto = accountService.getAllAccounts();

            log.info("Method 'handleGetAccounts' completed successful. All accounts sent");
            return accountsDto;
        } catch (Exception e) {
            log.error("Method 'handleGetAccounts' failed {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
