package com.bank.authorization.service;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.Audit;
import com.bank.authorization.mapper.AuditMapper;
import com.bank.authorization.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuditDto> getAllAudits() {
        return auditRepository.findAll().stream()
                .map(auditMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditDto> getAuditById(Long id) {
        return auditRepository.findById(id)
                .map(auditMapper::toDto);
    }

    @Override
    @Transactional
    public AuditDto save(AuditDto auditDto) {
        final Audit audit = auditMapper.toEntity(auditDto);
        final Audit savedAudit = auditRepository.save(audit);
        return auditMapper.toDto(savedAudit);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        auditRepository.deleteById(id);
    }
}
