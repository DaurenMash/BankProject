package com.bank.account.consumers;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.service.AccountService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountConsumer {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountConsumer(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @KafkaListener(topics = "account.create", groupId = "account-group")
    public void handleCreateAccount(AccountDto accountDto) {
        accountService.createNewAccount(accountMapper.setDataToEntity(accountDto));
    }

    @KafkaListener(topics = "account.update", groupId = "account-group")
    public void handleUpdateAccount(AccountDto accountDto) {
        accountService.updateCurrentAccount(accountDto.getId(), accountMapper.setDataToEntity(accountDto));
    }

    @KafkaListener(topics = "account.delete", groupId = "account-group")
    public void handleDeleteAccount(AccountDto accountDto) {
        accountService.deleteAccount(accountMapper.setDataToEntity(accountDto));
    }

    @KafkaListener(topics = "account.get", groupId = "account-group")
    public void handleGetAccounts() {
        List<Account> accounts = accountService.getAllAccounts();

    }

}
