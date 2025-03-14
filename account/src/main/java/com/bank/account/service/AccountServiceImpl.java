package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.exception.DataAccessException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.exception.IllegalArgumentException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional
    public AccountDto createNewAccount(AccountDto accountDto) {
        try {
            BigDecimal money = accountDto.getMoney();
            int comparisonResult = money.compareTo(BigDecimal.ZERO);
            accountDto.setNegativeBalance(comparisonResult < 0);

            Account accountExternal = accountRepository.save(accountMapper.toAccount(accountDto));

            log.info("New account created successfully");
            return accountMapper.toDto(accountExternal);
        } catch (DataAccessException e) {
            log.error("Database error while creating account", e);
            throw new DataAccessException("Database error while creating account");
        } catch (Exception e) {
            log.error("Unexpected error while while creating account", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AccountDto updateCurrentAccount(Long id, AccountDto accountDtoUpdated) {
        try {
            Account account = accountRepository.findAccountById(id);
            if (account == null) {
                throw new EntityNotFoundException("Account not found with id: " + id);
            }


            account.setAccountNumber(accountDtoUpdated.getAccountNumber());
            account.setMoney(accountDtoUpdated.getMoney());
            BigDecimal money = accountDtoUpdated.getMoney();
            int comparisonResult = money.compareTo(BigDecimal.ZERO);
            account.setNegativeBalance(comparisonResult < 0);

            account.setNegativeBalance(accountDtoUpdated.isNegativeBalance());
            account.setPassportId(accountDtoUpdated.getPassportId());
            account.setBankDetailsId(accountDtoUpdated.getBankDetailsId());
            account.setProfileId(accountDtoUpdated.getProfileId());

            Account accountExternal = accountRepository.save(account);
            accountRepository.flush();

            log.info("Account successfully updated ");
            return accountMapper.toDto(accountExternal);
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", id, e);
            throw new EntityNotFoundException("Account not found with id: " + id);
        } catch (DataAccessException e) {
            log.error("Database error while updating account: {}", id, e);
            throw new DataAccessException("Database error while updating account");
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data for account update: {}", id, e);
            throw new IllegalArgumentException("Invalid input data for account update");
        } catch (Exception e) {
            log.error("Unexpected error while updating account: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        try {
            Account account = accountRepository.findAccountById(id);
            if (account == null) {
                throw new EntityNotFoundException("Account not found with id: " + id);
            }
            accountRepository.delete(account);

            log.info("Account successfully deleted");
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", id, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while deleting account: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting account: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        try {
            AccountDto result = accountMapper.toDto(accountRepository.findAccountById(id));
            if (result == null) {
                throw new EntityNotFoundException("Account not found with id: " + id);
            }

            log.info("Account successfully retrieved ");
            return result;
        }  catch (EntityNotFoundException e) {
            log.error("Account not found: {}", id, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while getting account: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting account: {}", id, e);
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
        } catch (DataAccessException e) {
            log.error("Database error while getting all accounts", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting all accounts", e);
            throw e;
        }
    }
}
