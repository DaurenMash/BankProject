package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.AuditDto;
import com.bank.publicinfo.entity.BankDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.xml.bind.ValidationException;

public interface AuditService {

    Page<AuditDto> getAllAudits(Pageable pageable);

    AuditDto getAuditById(Long auditId);

    void deleteAuditById(Long auditId);

    <T> void updateAudit(T dto) throws JsonProcessingException, ValidationException;

    <T> void createAudit(T dto) throws JsonProcessingException;

    BankDetails findBankDetailsByBik(Long bik);

    AuditDto findAuditByEntityJsonId(Long entityId) throws JsonProcessingException, ValidationException;

}
