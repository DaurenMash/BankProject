package com.bank.profile.controller;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.service.AccountDetailsService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/account-details")
public class AccountDetailsController extends BasicCrudController<AccountDetailsService, AccountDetailsDto> {
    public AccountDetailsController(AccountDetailsService service) {
        super(service);
    }
}
