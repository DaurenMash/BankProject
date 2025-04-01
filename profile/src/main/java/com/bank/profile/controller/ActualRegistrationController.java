package com.bank.profile.controller;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.service.ActualRegistrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/actual-registrations")
public class ActualRegistrationController extends BasicCrudController<ActualRegistrationService, RegistrationDto> {
    public ActualRegistrationController(ActualRegistrationService service) {
        super(service);
    }
}
