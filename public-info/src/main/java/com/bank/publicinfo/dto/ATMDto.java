package com.bank.publicinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ATMDto implements Serializable {

    private Long id;
    private String address;
    private LocalTime startOfWork;
    private LocalTime endOfWork;
    private Boolean allHours;
    private Long branchId;

}
