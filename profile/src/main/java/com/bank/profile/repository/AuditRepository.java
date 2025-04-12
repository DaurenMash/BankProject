package com.bank.profile.repository;

import com.bank.profile.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    Optional<Audit> findByEntityTypeAndEntityJsonContains(String et, String sub);
}
