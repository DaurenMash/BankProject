package com.bank.publicinfo.repository;

import com.bank.publicinfo.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {

    BankDetails findByBik(Long bik);

}
