package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.CertificateDto;

import java.util.List;

public interface CertificateService {

    CertificateDto createNewCertificate(CertificateDto certificateDto);

    CertificateDto updateCertificate(CertificateDto newCertificateDto);

    void deleteCertificate(Long certificateId);

    List<CertificateDto> getCertificatesByBankDetails(Long bankDetailsId);

    CertificateDto getCertificateById(Long certificateId);

}
