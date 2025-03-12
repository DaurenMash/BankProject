package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional
    public void createNewAccount(AccountDto accountDto) {
        try {
            accountRepository.save(accountMapper.toAccount(accountDto));

            log.info("New account created successfully");
        } catch (Exception e) {
            log.error("Service. Failed to create new account {}", e.getMessage());
        }

    }

    @Override
    @Transactional
    public AccountDto updateCurrentAccount(Long id, AccountDto accountDtoUpdated) {
        try {
            Account account = accountRepository.findAccountById(id);

            account.setAccountNumber(accountDtoUpdated.getAccountNumber());
            account.setMoney(accountDtoUpdated.getMoney());
            account.setPassportId(accountDtoUpdated.getPassportId());
            account.setNegativeBalance(accountDtoUpdated.isNegativeBalance());
            account.setBankDetailsId(accountDtoUpdated.getBankDetailsId());
            account.setProfileId(accountDtoUpdated.getProfileId());

            accountRepository.save(account);
            accountRepository.flush();

            log.info("Account successfully updated ");
            return accountMapper.toDto(account);
        } catch (Exception e) {
            log.error("Service. Failed to update account {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAccount(AccountDto accountDto) {
        try {
            accountRepository.delete(accountMapper.toAccount(accountDto));

            log.info("Account successfully deleted ");
        } catch (Exception e) {
            log.error("Service. Failed to delete account {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        try {
            AccountDto result = accountMapper.toDto(accountRepository.findAccountById(id));
            log.info("Account successfully retrieved ");
            return result;
        } catch (Exception e) {
            log.error("Service. Failed to retrieve account {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        try {
            List<AccountDto> result = accountRepository
                    .findAll()
                    .stream()
                    .map(accountMapper::toDto)
                    .collect(Collectors.toList());

            if (result != null) {
                log.info("List of accounts successfully retrieved");
            } else {
                log.warn("List of accounts is null");
            }
            return result;
        } catch (Exception e) {
            log.error("Service. Failed to retrieve accounts {}", e.getMessage());
            throw e;
        }
    }
}
