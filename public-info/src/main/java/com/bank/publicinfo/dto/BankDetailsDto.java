package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import static com.bank.publicinfo.entity.BankDetails.CITY_MAX_LENGTH;
import static com.bank.publicinfo.entity.BankDetails.JOINT_STOCK_COMPANY_MAX_LENGTH;
import static com.bank.publicinfo.entity.BankDetails.NAME_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsDto implements Serializable {

    private Long id;

    @NotNull
    private Long bik;

    @NotNull
    private Long inn;

    @NotNull
    private Long kpp;

    @NotNull
    private Long corAccount;

    @Size(max = CITY_MAX_LENGTH)
    @NotNull
    private String city;

    @Size(max = JOINT_STOCK_COMPANY_MAX_LENGTH)
    @NotNull
    private String jointStockCompany;

    @Size(max = NAME_MAX_LENGTH)
    @NotNull
    private String name;

}
