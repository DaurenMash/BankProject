package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.ATMDto;

import java.util.List;

public interface ATMService {

    ATMDto createNewATM(ATMDto atmDto);

    ATMDto updateATM(ATMDto atmDto);

    void deleteATMById(Long id);

    List<ATMDto> getATMs(Long branchId);

    ATMDto getATMById(Long atmId);

}
