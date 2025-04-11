package com.bank.profile.controller;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.service.RegistrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/registrations")
public class TestRegistrationController extends BasicCrudController<RegistrationService, RegistrationDto> {
    public TestRegistrationController(RegistrationService service) {
        super(service);
    }
}
