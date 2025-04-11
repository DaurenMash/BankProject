package com.bank.antifraud.repository;

import com.bank.antifraud.model.SuspiciousPhoneTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousPhoneTransferRepository extends JpaRepository<SuspiciousPhoneTransfer, Integer> {
}
