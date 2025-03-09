package com.bank.account.controller;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountsController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountsController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountDto>> getAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountDto> accountsDto = accounts.stream()
                .map(accountMapper::setDataToDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(accountsDto);
    }

    @GetMapping(value = "/account/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        AccountDto accountDto = accountMapper.setDataToDto(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountDto);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Void> saveAccount(@Valid @RequestBody AccountDto accountDto) {
        try {
            Account account = accountMapper.setDataToEntity(accountDto);
            accountService.createNewAccount(account);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/update_account/{id}")
    public ResponseEntity<Void> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        try {
            Account account = accountMapper.setDataToEntity(accountDto);
            accountService.updateCurrentAccount(id, account);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            Account accountForDelete = accountService.getAccountById(id);
            if (accountForDelete == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
            }
            accountService.deleteAccount(accountForDelete);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
