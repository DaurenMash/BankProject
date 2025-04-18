package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalTime;
import static com.bank.publicinfo.entity.Branch.ADDRESS_MAX_LENGTH;
import static com.bank.publicinfo.entity.Branch.CITY_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchDto implements Serializable {

    private Long id;

    @Size(max = ADDRESS_MAX_LENGTH)
    @NotNull
    private String address;

    @NotNull
    private Long phoneNumber;

    @Size(max = CITY_MAX_LENGTH)
    @NotNull
    private String city;

    @NotNull
    private LocalTime startOfWork;

    @NotNull
    private LocalTime endOfWork;

}
