package com.bank.publicinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsDto implements Serializable {

    private Long id;
    private Long bik;
    private Long inn;
    private Long kpp;
    private Long corAccount;
    private String city;
    private String jointStockCompany;
    private String name;

}
