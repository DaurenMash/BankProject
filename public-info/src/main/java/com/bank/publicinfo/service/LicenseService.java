package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.LicenseDto;

import java.util.List;

public interface LicenseService {

    LicenseDto createNewLicense(LicenseDto licenseDto);

    LicenseDto updateLicense(LicenseDto licenseDto);

    void deleteLicense(Long licenseId);

    List<LicenseDto> getLicensesByBankDetails(Long bankDetailsId);

    LicenseDto getLicenseById(Long licenseId);


}

