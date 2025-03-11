package com.bank.account.mapper;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class AccountMapper {
    public AccountDto setDataFromEntityToDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setId(account.getId());
        accountDto.setMoney(account.getMoney());
        accountDto.setPassportId(account.getPassportId());
        accountDto.setNegativeBalance(account.isNegativeBalance());
        accountDto.setBankDetailsId(account.getBankDetailsId());
        accountDto.setProfileId(account.getProfileId());
        return accountDto;
    }

    public Account setDataToEntity(AccountDto accountDto) {
        Account account = new Account();

        account.setAccountNumber(accountDto.getAccountNumber());
        account.setId(accountDto.getId());
        account.setMoney(accountDto.getMoney());
        account.setPassportId(accountDto.getPassportId());
        account.setNegativeBalance(accountDto.isNegativeBalance());
        account.setBankDetailsId(accountDto.getBankDetailsId());
        account.setProfileId(accountDto.getProfileId());
        return account;
    }
}
