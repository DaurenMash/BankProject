package com.bank.profile.config;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.AccountDetails;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Profile;
import com.bank.profile.entity.Registration;
import com.bank.profile.mapper.RegistrationMapper;
import com.bank.profile.repository.*;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
@org.springframework.context.annotation.Profile("mock-data")
public class MockConfiguration {

    public MockConfiguration(
            ProfileRepository profileRep,
            PassportRepository passportRep,
            RegistrationRepository registrationRep,
            ActualRegistrationRepository actualRegistrationRep,
            AccountDetailsRepository accountDetailsRep,
            AuditRepository auditRep,
            RegistrationMapper registrationMapper
    ) {
        List<RegistrationDto> regDtos = List.of(
                new RegistrationDto(null, "Great Britain", "London", 123123l, "Region 1", "District 1", "Locality 1", "Street 1", "house 1", "block 1", "flat 666"),
                new RegistrationDto(null, "Australia", "London", 432432l, "Region 22", "District 22", "Locality 22", "Street 22", "house 22", "block 22", "flat 222")
        );
        List<Registration> registrations = registrationRep.saveAll(
                regDtos.stream().map(registrationMapper::toRegistration).toList());
        List<ActualRegistration> actualRegistrations = actualRegistrationRep.saveAll(
                regDtos.stream().map(registrationMapper::toActualRegistration).toList());


        List<Passport> passports = passportRep.saveAll(List.of(
                new Passport(null, 1234, 123456l, "Statham", "Jason", null, "m", LocalDate.of( 1950, 5, 5), "Snatch av., London", "Guy Ritchie Department", LocalDate.of(2000, 8, 23), 666, LocalDate.of(2100, 8, 23), registrations.get(0)),
                new Passport(null, 4321, 654321l, "Sina", "John", "Whatchout", "m", LocalDate.of( 1970, 7, 7), "MMA Arena av., USA", "Meme Department", LocalDate.of(2010, 1, 1), 333, LocalDate.of(2200, 1, 11), registrations.get(1))
        ));


        List<Profile> profiles = profileRep.saveAll(List.of(
           new Profile(null, 79008001234l, "boss@gym.com", "Jason Statham", 123123123l, 321321321l, actualRegistrations.get(0), passports.get(0)),
           new Profile(null, 75003004321l, "wrestler@gym.com", "John Sina", 345345345l, 543543543l, actualRegistrations.get(1), passports.get(1))
        ));

        accountDetailsRep.saveAll(List.of(
                new AccountDetails(null, 1l, profiles.get(0)),
                new AccountDetails(null, 2l, profiles.get(1))
        ));
    }
}
