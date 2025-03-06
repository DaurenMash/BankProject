package com.bank.account.service;

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
    private final AuditRepository auditRepository;
    private final AuditService auditService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AuditRepository auditRepository,
                              AuditService auditService) {
        this.accountRepository = accountRepository;
        this.auditRepository = auditRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public void createNewAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateCurrentAccount(Account account) {
        Account existingAccount = accountRepository.findAccountByAccountNumber(account.getAccountNumber());

        existingAccount.setAccountNumber(account.getAccountNumber());
        existingAccount.setMoney(account.getMoney());
        existingAccount.setPassportId(account.getPassportId());
        existingAccount.setNegativeBalance(account.isNegativeBalance());
        existingAccount.setBankDetailsId(account.getBankDetailsId());
        existingAccount.setProfileId(account.getProfileId());

        accountRepository.save(existingAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(int accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
