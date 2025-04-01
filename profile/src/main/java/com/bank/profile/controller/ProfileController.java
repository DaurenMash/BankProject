package com.bank.profile.controller;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.service.ProfileService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/profiles")
public class ProfileController extends BasicCrudController<ProfileService, ProfileDto> {
    public ProfileController(ProfileService profileService) {
        super(profileService);
    }
}
