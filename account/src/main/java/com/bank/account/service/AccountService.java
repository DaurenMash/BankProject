package com.bank.account.service;

import com.bank.account.entity.Account;

import java.util.List;

public interface AccountService {
    void createNewAccount(Account account);

    void updateCurrentAccount(Account account);

    void deleteAccount(Account account);

    Account getAccountById(int id);

    List<Account> getAllAccounts();
}
