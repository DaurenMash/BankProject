package com.bank.profile.service.impl;

import com.bank.profile.entity.Audit;
import com.bank.profile.kafka.producer.AuditProducer;
import com.bank.profile.mapper.AuditMapper;
import com.bank.profile.repository.AuditRepository;
import com.bank.profile.service.AuditService;
import com.bank.profile.util.audit.EntityType;
import com.bank.profile.util.audit.OperationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditProducer auditProducer;
    private final AuditMapper auditMapper;

    private final ObjectWriter objWriter;

    public AuditServiceImpl(AuditRepository auditRepository, AuditProducer auditProducer, AuditMapper auditMapper, ObjectMapper objMapper) {
        this.auditRepository = auditRepository;
        this.auditProducer = auditProducer;
        this.auditMapper = auditMapper;
        this.objWriter = objMapper.writer();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(Object value) {

        String str = null;
        try {
            str = objWriter.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String principal = "user";

        Audit entity = auditRepository.save(new Audit(
                null,
                EntityType.valueOf(value.getClass().getSimpleName()).toString(),
                OperationType.Create.toString(),
                principal,
                null,
                LocalDateTime.now(),
                null,
                str,
                null
        ));

        auditProducer.sendAudit(auditMapper.toDto(entity));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(Object value) {
        String str = null;
        try {
            str = objWriter.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String principal = "user";

        Pattern pattern = Pattern.compile("(\"id\":-?\\d+,)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        matcher.find();

        String id = matcher.group(1);

        Optional<Audit> auditOp = auditRepository.findByEntityTypeAndEntityJsonContains(
                EntityType.valueOf(value.getClass().getSimpleName()).toString(),
                id
        );

        if (auditOp.isEmpty())
            return;

        Audit audit = auditOp.get();

        audit.setModifiedBy(principal);
        audit.setModifiedAt(LocalDateTime.now());
        audit.setNewEntityJson(str);

        auditRepository.save(audit);

        auditProducer.sendAudit(auditMapper.toDto(audit));
    }
}