package com.bank.profile.controller;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.service.PassportService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/passports")
public class PassportController extends BasicCrudController<PassportService, PassportDto> {
    public PassportController(PassportService service) {
        super(service);
    }
}
