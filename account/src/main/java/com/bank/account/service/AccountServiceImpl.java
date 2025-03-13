package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.producers.AccountProducer;
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
    private final AccountProducer accountProducer;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountMapper accountMapper,
                              AccountProducer accountProducer) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountProducer = accountProducer;
    }

    @Override
    @Transactional
    public AccountDto createNewAccount(AccountDto accountDto) {
        try {
            Account accountExternal = accountRepository.save(accountMapper.toAccount(accountDto));
            accountProducer.sendCreatedAccountExternalEvent(accountMapper.toDto(accountExternal));

            log.info("New account created successfully");
            return accountMapper.toDto(accountExternal);
        } catch (Exception e) {
            log.error("Service. Failed to create new account {}", e.getMessage());
            throw e;
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

            Account accountExternal = accountRepository.save(account);
            accountRepository.flush();
            accountProducer.sendUpdatedAccountExternalEvent(accountMapper.toDto(accountExternal));

            log.info("Account successfully updated ");
            return accountMapper.toDto(accountExternal);
        } catch (Exception e) {
            log.error("Service. Failed to update account {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        try {
            Account account = accountRepository.findAccountById(id);
            accountRepository.delete(account);
            accountProducer.sendDeletedAccountExternalEvent("Account successfully deleted");

            log.info("Account successfully deleted");
        } catch (Exception e) {
            log.error("Service. Failed to delete account {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        try {
            AccountDto result = accountMapper.toDto(accountRepository.findAccountById(id));
            accountProducer.sendGetOneAccountByIdExternalEvent(result);

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
            accountProducer.sendGetAccountsExternalEvent(result);

            return result;
        } catch (Exception e) {
            log.error("Service. Failed to retrieve accounts {}", e.getMessage());
            throw e;
        }
    }
}
