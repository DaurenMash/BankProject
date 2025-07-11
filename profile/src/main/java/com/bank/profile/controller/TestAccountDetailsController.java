package com.bank.profile.controller;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.service.AccountDetailsService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/account-details")
public class TestAccountDetailsController extends BasicCrudController<AccountDetailsService, AccountDetailsDto> {
    public TestAccountDetailsController(AccountDetailsService service) {
        super(service);
    }
}
