package com.bank.publicinfo.repository;

import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.Certificate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findCertificatesByBankDetails(@NotNull BankDetails bankDetails);

    void deleteCertificateByBankDetailsId(@NotNull Long bankDetailsId);

}
