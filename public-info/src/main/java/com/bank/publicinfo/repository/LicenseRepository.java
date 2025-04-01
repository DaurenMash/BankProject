package com.bank.publicinfo.repository;

import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.License;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findLicensesByBankDetails(@NotNull BankDetails bankDetails);

    void deleteLicensesByBankDetails_Id(@NotNull Long id);

}
