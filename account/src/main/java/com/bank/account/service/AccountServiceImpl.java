package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void createNewAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateCurrentAccount(Long id, Account accountUpdated) {
        Account account = accountRepository.findAccountById(id);

        account.setAccountNumber(accountUpdated.getAccountNumber());
        account.setMoney(accountUpdated.getMoney());
        account.setPassportId(accountUpdated.getPassportId());
        account.setNegativeBalance(accountUpdated.isNegativeBalance());
        account.setBankDetailsId(accountUpdated.getBankDetailsId());
        account.setProfileId(accountUpdated.getProfileId());

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findAccountById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
