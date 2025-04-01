package com.bank.account.service;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.exception.custom_exceptions.EntityNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountRepository;
import com.bank.account.validator.AccountValidator;
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
    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found with id: ";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    /**
     * Создает новый банковский счет на основе переданных данных
     *
     * @param accountDto аккаунт с переданными данными для создания счета
     * @return AccountDto созданного счета, внесенного в БД
     */
    @Override
    @Transactional
    public AccountDto createNewAccount(AccountDto accountDto) {
        accountValidator.validate(accountDto);

        accountDto.setNegativeBalance(isNegativeBalance(accountDto));
        final Account accountExternal = accountRepository.save(accountMapper.toAccount(accountDto));
        log.info("New account with account number: {} created successfully", accountDto.getAccountNumber());
        return accountMapper.toDto(accountExternal);
    }

    /**
     * Обновляет данные существующего аккаунта
     *
     * @param id идентификатор счета в БД
     * @param accountDtoUpdated AccountDto с обновленными данными
     * @return AccountDto с обновленными данными из БД
     */

    @Override
    @Transactional
    public AccountDto updateCurrentAccount(Long id, AccountDto accountDtoUpdated) {
        accountValidator.validateForUpdate(id, accountDtoUpdated);

        final Account account = accountRepository.findAccountById(id);
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
    }

    /**
     * Удаляет аккаунт из БД
     *
     * @param id существующего аккаунта в БД
     * @throws EntityNotFoundException если аккаунт не найден в базе данных
     */
    @Override
    @Transactional
    public void deleteAccount(Long id) {
        final Account account = accountRepository.findAccountById(id);
        if (account == null) {
            log.error("Account not found, cannot delete: {}", id);
            throw new EntityNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + id);
        }
        accountRepository.delete(account);
        log.info("Account successfully deleted");
    }

    /**
     * Возвращает аккаунт из БД по номеру id
     * @param id идентификатор аккаунта в БД
     * @return AccountDto по номеру идентификатора
     * @throws EntityNotFoundException если аккаунт не найден в базе данных
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        final Account result = accountRepository.findAccountById(id);
        if (result == null) {
            log.error("Account not found, cannot get account by id: {}", id);
            throw new EntityNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + id);
        }

        log.info("Account with id={} successfully retrieved", id);
        return accountMapper.toDto(result);
    }

    /**
     * Возвращает список всех аккаунтов в БД
     *
     * @return List<AccountDto> с информацией о всех аккаунтах
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isNegativeBalance(AccountDto accountWithMoney) {
        final BigDecimal money = accountWithMoney.getMoney();
        return money.compareTo(BigDecimal.ZERO) < 0;
    }
}
