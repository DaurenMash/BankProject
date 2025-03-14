package com.bank.account.consumers;

import com.bank.account.dto.AccountDto;
import com.bank.account.exception.GlobalExceptionHandler;
import com.bank.account.producers.AccountProducer;
import com.bank.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccountConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountService accountService;
    private final AccountProducer accountProducer;
    private final GlobalExceptionHandler globalExceptionHandler;

    public AccountConsumer(AccountService accountService,
                           GlobalExceptionHandler globalExceptionHandler,
                           AccountProducer accountProducer) {
        this.accountService = accountService;
        this.globalExceptionHandler = globalExceptionHandler;
        this.accountProducer = accountProducer;
    }

    @KafkaListener (topics = "account.create", groupId = "account-group")
    public void handleCreateAccount(@Payload String json) {
        try {
            AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            AccountDto responseAccount = accountService.createNewAccount(accountDto);
            accountProducer.sendCreatedAccountExternalEvent(responseAccount);

            log.info("Method 'handleCreateAccount' completed successful");
        } catch (Exception e) {
            log.error("Method 'handleCreateAccount' failed {}", e.getMessage());
            globalExceptionHandler.handleException(e, "error.logs");
        }
    }

    @KafkaListener(topics = "account.update", groupId = "account-group")
    public void handleUpdateAccount(@Payload String json) {
        try {
            AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            AccountDto responseAccount = accountService.updateCurrentAccount(accountDto.getId(), accountDto);

            accountProducer.sendUpdatedAccountExternalEvent(responseAccount);

            log.info("Method 'handleUpdateAccount' completed  successful ");
        } catch (Exception e) {
            log.error("Method 'handleUpdateAccount' failed {}", e.getMessage());
            globalExceptionHandler.handleException(e, "error.logs");
        }
    }

    @KafkaListener(topics = "account.delete", groupId = "account-group")
    public void handleDeleteAccount(@Payload String json) {
        try {
            Long accountId;

            if (json.matches("\\d+")) {
                accountId = Long.parseLong(json);
            } else {
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                accountId = Long.valueOf(map.get("id").toString());
            }

            accountService.deleteAccount(accountId);
            accountProducer.sendDeletedAccountExternalEvent(String.format("Account with id: %s successfully deleted", accountId));
            log.info("Method 'handleDeleteAccount' completed successful ");
        } catch (Exception e) {
            log.error("Method 'handleDeleteAccount' failed {}", e.getMessage());
            globalExceptionHandler.handleException(e, "error.logs");
        }
    }

    @KafkaListener(topics = "account.get", groupId = "account-group")
    public void handleGetAccounts() {
        try {
            List<AccountDto> accountsDto = accountService.getAllAccounts();
            accountProducer.sendGetAccountsExternalEvent(accountsDto);

            log.info("Method 'handleGetAccounts' completed successful. All accounts sent");
        } catch (Exception e) {
            log.error("Method 'handleGetAccounts' failed {}", e.getMessage());
            globalExceptionHandler.handleException(e, "error.logs");
        }
    }

    @KafkaListener(topics = "account.getById", groupId = "account-group")
    public void handleGetByIdAccount(String json) {
        try {
            Long accountId;

            if (json.matches("\\d+")) {
                accountId = Long.parseLong(json);
            } else {
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                accountId = Long.valueOf(map.get("id").toString());
            }

            AccountDto resultAccount = accountService.getAccountById(accountId);
            accountProducer.sendGetOneAccountByIdExternalEvent(resultAccount);

            log.info("Method 'handleGetByIdAccount' completed successful. Account found: {}", resultAccount);
        } catch (Exception e) {
            log.error("Method 'handleGetByIdAccount' failed {}", e.getMessage());
            globalExceptionHandler.handleException(e, "error.logs");
        }
    }
}
