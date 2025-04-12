package com.bank.publicinfo.repository;

import com.bank.publicinfo.entity.Audit;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    Audit findByEntityJson(@NotNull String entityJson);

    Audit findAuditByEntityType(@NotNull String entityType);

    List<Audit> findAllByEntityJson(@NotNull String entityJson);

}
