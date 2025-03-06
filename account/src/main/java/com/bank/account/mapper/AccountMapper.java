package com.bank.account.mapper;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;

public class AccountMapper {
    public AccountDto setDataToDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setId(account.getId());
        accountDto.setMoney(account.getMoney());
        accountDto.setPassportId(account.getPassportId());
        accountDto.setNegativeBalance(account.isNegativeBalance());
        return accountDto;
    }

    public Account setDataToEntity(AccountDto accountDto) {
        Account account = new Account();

        account.setAccountNumber(accountDto.getAccountNumber());
        account.setId(accountDto.getId());
        account.setMoney(accountDto.getMoney());
        account.setPassportId(accountDto.getPassportId());
        account.setNegativeBalance(accountDto.isNegativeBalance());
        return account;
    }
}
