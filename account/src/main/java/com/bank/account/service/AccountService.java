package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;

import java.util.List;

public interface AccountService {
    void createNewAccount(Account account);

    Account updateCurrentAccount(Long id, Account accountUpdated);

    void deleteAccount(Account account);

    Account getAccountById(Long id);

    List<Account> getAllAccounts();
}
