package com.bank.profile.controller;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.service.ProfileService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/profiles")
public class TestProfileController extends BasicCrudController<ProfileService, ProfileDto> {
    public TestProfileController(ProfileService profileService) {
        super(profileService);
    }
}
