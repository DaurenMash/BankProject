package com.bank.profile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private Long id;

    @NotNull
    private Long phoneNumber;

    @Size(max = 264)
    private String email;

    @Size(max = 370)
    private String nameOnCard;

    private Long inn;

    private Long snils;

    private RegistrationDto actualRegistration;

    @NotNull
    private PassportDto passport;

    public static ProfileDto Empty()
    {
        ProfileDto dto = new ProfileDto();
        dto.phoneNumber = 0l;
        dto.email = "empty@email.com";
        dto.nameOnCard = "";
        dto.inn = null;
        dto.snils = null;
        dto.actualRegistration = null;
        dto.passport = null;

        return dto;
    }
}