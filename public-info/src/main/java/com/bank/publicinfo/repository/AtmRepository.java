package com.bank.publicinfo.repository;

import com.bank.publicinfo.entity.ATM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtmRepository extends JpaRepository<ATM, Long> {

    List<ATM> findByBranchId(Long branchId);

}
