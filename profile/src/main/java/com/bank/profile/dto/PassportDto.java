package com.bank.profile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassportDto {
    private Long id;

    @NotNull
    private Integer series;

    @NotNull
    private Long number;

    @Size(max = 255)
    @NotNull
    private String lastName;

    @Size(max = 255)
    @NotNull
    private String firstName;

    @Size(max = 255)
    private String middleName;

    @Size(max = 3)
    @NotNull
    private String gender;

    @NotNull
    private LocalDate birthDate;

    @Size(max = 480)
    @NotNull
    private String birthPlace;

    @NotNull
    private String issuedBy;

    @NotNull
    private LocalDate dateOfIssue;

    @NotNull
    private Integer divisionCode;

    private LocalDate expirationDate;

    @NotNull
    private RegistrationDto registration;

    public static PassportDto Empty()
    {
        PassportDto dto = new PassportDto();

        LocalDate stubDate = LocalDate.of(1800, 1, 1);

        dto.series = 0;
        dto.number = 0l;
        dto.lastName = "";
        dto.firstName = "";
        dto.middleName = null;
        dto.gender = "";
        dto.birthDate = stubDate;
        dto.birthPlace = "";
        dto.issuedBy = "";
        dto.dateOfIssue = stubDate;
        dto.divisionCode = 0;
        dto.expirationDate = stubDate;
        dto.registration = null;

        return dto;
    }
}