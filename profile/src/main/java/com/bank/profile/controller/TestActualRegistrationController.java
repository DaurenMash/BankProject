package com.bank.profile.controller;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.service.ActualRegistrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/actual-registrations")
public class TestActualRegistrationController extends BasicCrudController<ActualRegistrationService, RegistrationDto> {
    public TestActualRegistrationController(ActualRegistrationService service) {
        super(service);
    }
}
