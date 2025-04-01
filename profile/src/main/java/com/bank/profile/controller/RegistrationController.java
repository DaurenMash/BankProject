package com.bank.profile.controller;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.service.RegistrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/registrations")
public class RegistrationController extends BasicCrudController<RegistrationService, RegistrationDto> {
    public RegistrationController(RegistrationService service) {
        super(service);
    }
}
