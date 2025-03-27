package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.exception.DataAccessException;
import com.bank.account.exception.EntityNotFoundException;
import com.bank.account.exception.IllegalArgumentException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления банковскими аккаунтами.
 * Предоставляет методы для создания, изменения, удаления и получения информации об аккаунтах.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final String accountNotFoundMessage = "Account not found with id: ";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

   /**
     * Создает новый банковский счет на основе переданных данных
     *
     * @param accountDto аккаунт с переданными данными для создания счета
     * @return AccountDto созданного счета, внесенного в БД
     * @throws DataAccessException если произошла непредвиденная ошибка доступа к БД
     * @throws RuntimeException если произошла непредвиденная ошибка
     */
    @Override
    @Transactional
    public AccountDto createNewAccount(AccountDto accountDto) {
        try {
            if (accountRepository.existsAccountByAccountNumber(accountDto.getAccountNumber())) {
                throw new IllegalArgumentException("Account number already exists");
            }
            if (accountRepository.existsAccountByBankDetailsId(accountDto.getBankDetailsId())) {
                throw new IllegalArgumentException("Bank details id already exists");
            }

            accountDto.setNegativeBalance(isNegativeBalance(accountDto));

            final Account accountExternal = accountRepository.save(accountMapper.toAccount(accountDto));

            log.info("New account with account number: {} created successfully", accountDto.getAccountNumber());
            return accountMapper.toDto(accountExternal);
        } catch (DataAccessException e) {
            log.error("Database error while creating account with account number:{}", accountDto.getAccountNumber(), e);
            throw new DataAccessException("Database error while creating account with account number:" +
                    accountDto.getAccountNumber());
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data for account creation: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while while creating account with account number: {}",
                    accountDto.getAccountNumber(), e);
            throw new RuntimeException("Unexpected error while creating account with account number" +
                    accountDto.getAccountNumber(), e);
        }
    }

    /**
     * Обновляет данные существующего аккаунта
     *
     * @param id идентификатор счета в БД
     * @param accountDtoUpdated AccountDto с обновленными данными
     * @return AccountDto с обновленными данными из БД
     * @throws EntityNotFoundException если аккаунт с таким id не найден в БД
     * @throws DataAccessException если произошла непредвиденная ошибка доступа к БД
     * @throws IllegalArgumentException если переданные некорректные данные в accountDtoUpdated
     * @throws RuntimeException если произошла непредвиденная ошибка
     */

    @Override
    @Transactional
    public AccountDto updateCurrentAccount(Long id, AccountDto accountDtoUpdated) {
        try {
            final Account account = accountRepository.findAccountById(id);
            if (account == null) {
                throw new EntityNotFoundException(accountNotFoundMessage + id);
            }

            if (accountRepository.existsAccountByAccountNumber(accountDtoUpdated.getAccountNumber()) ||
                    accountRepository.existsAccountByBankDetailsId(accountDtoUpdated.getBankDetailsId())) {
                final Account existingAccByAccountNumber = accountRepository
                        .findAccountByAccountNumber(accountDtoUpdated.getAccountNumber());
                final Account existingAccByBankDetailsId = accountRepository
                        .findAccountByBankDetailsId(accountDtoUpdated.getBankDetailsId());

                final Long existingAccByAccountNumberId = existingAccByAccountNumber.getId();
                final Long existingAccByBankDetailsIdId = existingAccByBankDetailsId.getId();

                if (!existingAccByAccountNumberId.equals(id) || !existingAccByBankDetailsIdId.equals(id)) {
                    throw new IllegalArgumentException("Account with same AccountNumber or " +
                            "BankDetailsId already exists. " + "AccountNumber is " +
                            accountDtoUpdated.getAccountNumber() + ", BankDetailsId is " +
                            accountDtoUpdated.getBankDetailsId());
                }
            }

            account.setAccountNumber(accountDtoUpdated.getAccountNumber());
            account.setMoney(accountDtoUpdated.getMoney());
            account.setNegativeBalance(isNegativeBalance(accountDtoUpdated));
            account.setPassportId(accountDtoUpdated.getPassportId());
            account.setBankDetailsId(accountDtoUpdated.getBankDetailsId());
            account.setProfileId(accountDtoUpdated.getProfileId());

            final Account accountExternal = accountRepository.save(account);
            accountRepository.flush();

            log.info("Account successfully updated ");
            return accountMapper.toDto(accountExternal);
        } catch (EntityNotFoundException e) {
            log.error("Account with id={} is not found.", id, e);
            throw new EntityNotFoundException(accountNotFoundMessage + id);
        } catch (DataAccessException e) {
            log.error("Database error while updating account: {}", id, e);
            throw new DataAccessException("Database error while updating account with id=" + id);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input data for account update: {}", id, e);
            throw new IllegalArgumentException("Invalid input data for account update" + id);
        } catch (Exception e) {
            log.error("Unexpected error while updating account: {}", id, e);
            throw new RuntimeException("Unexpected error while updating account with id=" + id, e);
        }
    }

    /**
     * Удаляет аккаунт из БД
     *
     * @param id существующего аккаунта в БД
     * @throws EntityNotFoundException если аккаунт не найден в базе данных
     * @throws DataAccessException если произошла непредвиденная ошибка во время работы с БД
     * @throws RuntimeException если произошла непредвиденная ошибка
     */
    @Override
    @Transactional
    public void deleteAccount(Long id) {
        try {
            final Account account = accountRepository.findAccountById(id);
            if (account == null) {
                throw new EntityNotFoundException(accountNotFoundMessage + id);
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
            throw new RuntimeException("Unexpected error while deleting account with id=" + id, e);
        }
    }

    /**
     * Возвращает аккаунт из БД по номеру id
     * @param id идентификатор аккаунта в БД
     * @return AccountDto по номеру идентификатора
     * @throws EntityNotFoundException если аккаунт не найден в базе данных
     * @throws DataAccessException если произошла непредвиденная ошибка доступа к БД
     * @throws RuntimeException если произошла непредвиденная ошибка
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        try {
            final AccountDto result = accountMapper.toDto(accountRepository.findAccountById(id));
            if (result == null) {
                throw new EntityNotFoundException(accountNotFoundMessage + id);
            }

            log.info("Account with id={} successfully retrieved", id);
            return result;
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", id, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while getting account: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting account: {}", id, e);
            throw new RuntimeException("Unexpected error while getting account with id=" + id, e);
        }
    }

    /**
     * Возвращает список всех аккаунтов в БД
     *
     * @return List<AccountDto> с информацией о всех аккаунтах
     * @throws DataAccessException в случае непредвиденной ошибки при работе с БД
     * @throws RuntimeException если произошла непредвиденная ошибка
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        try {
            final List<AccountDto> result = accountRepository
                    .findAll()
                    .stream()
                    .map(accountMapper::toDto)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                log.info("List of accounts successfully retrieved");
            } else {
                log.warn("List of accounts is empty");
            }
            return result;
        } catch (DataAccessException e) {
            log.error("Database error while getting all accounts", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting all accounts", e);
            throw new RuntimeException("Unexpected error while getting accounts list", e);
        }
    }

    private boolean isNegativeBalance(AccountDto accountWithMoney) {
        BigDecimal money = accountWithMoney.getMoney();
        return money.compareTo(BigDecimal.ZERO) < 0;
    }
}
