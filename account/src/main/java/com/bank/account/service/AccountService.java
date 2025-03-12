package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;

import java.util.List;

public interface AccountService {
    void createNewAccount(AccountDto accountDto);

    AccountDto updateCurrentAccount(Long id, AccountDto accountDtoUpdated);

    void deleteAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    List<AccountDto> getAllAccounts();
}
