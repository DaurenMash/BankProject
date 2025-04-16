package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalTime;
import static com.bank.publicinfo.entity.ATM.MAX_ADDRESS_LENGTH;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ATMDto implements Serializable {

    private Long id;

    @Size(max = MAX_ADDRESS_LENGTH)
    @NotNull
    private String address;

    private LocalTime startOfWork;

    private LocalTime endOfWork;

    @NotNull
    private Boolean allHours;

    @NotNull
    private Long branchId;

}
