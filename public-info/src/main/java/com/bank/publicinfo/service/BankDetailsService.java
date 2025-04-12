package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.exception.ValidationException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankDetailsService {

    BankDetailsDto createNewBankDetails(BankDetailsDto bankDetailsDto) throws ValidationException;

    BankDetailsDto updateBankDetails(BankDetailsDto bankDetailsDto) throws ValidationException;

    void deleteBankDetailsById(Long bankId) throws ValidationException;

    List<BankDetailsDto> getAllBanksDetails(Pageable pageable);

    BankDetailsDto getBankDetailsById(Long bankId) throws ValidationException;

}
