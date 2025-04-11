package com.bank.profile.service.impl;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.dto.ProfileDto;
import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Profile;
import com.bank.profile.exception.EntityNotUniqueException;
import com.bank.profile.mapper.ProfileMapper;
import com.bank.profile.repository.ProfileRepository;
import com.bank.profile.service.ActualRegistrationService;
import com.bank.profile.service.PassportService;
import com.bank.profile.service.ProfileService;
import com.bank.profile.service.RegistrationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final ActualRegistrationService actualRegistrationService;
    private final PassportService passportService;
    private final RegistrationService registrationService;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProfileDto> getAll() {
        return profileRepository.findAll().stream().map(profileMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto get(Long id) {
        return profileMapper.toDto(profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Profile.class.getSimpleName())));
    }

    @Override
    @Transactional
    public ProfileDto createEmpty() {
        RegistrationDto registrationDto = RegistrationDto.Empty();
        registrationDto = registrationService.create(registrationDto);

        PassportDto passportDto = PassportDto.Empty();
        passportDto.setRegistration(registrationDto);
        passportDto = passportService.create(passportDto);

        ProfileDto profileDto = ProfileDto.Empty();
        profileDto.setPassport(passportDto);

        return create(profileDto);
    }

    @Override
    @Transactional
    public ProfileDto create(@Valid ProfileDto dto) {
        if (dto.getSnils() != null && profileRepository.findBySnils(dto.getSnils()).isPresent()) {
            throw new EntityNotUniqueException(Profile.class.getName(), "snils");
        }
        if (dto.getInn() != null && profileRepository.findBySnils(dto.getInn()).isPresent()) {
            throw new EntityNotUniqueException(Profile.class.getName(), "inn");
        }

        Profile profile = profileMapper.toEntity(dto);

        return profileMapper.toDto( profileRepository.save( profile ) );
    }

    @Override
    @Transactional
    public ProfileDto update(Long id, @Valid ProfileDto dto) {
        profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Profile.class.getSimpleName()));

        profileRepository.findBySnils(dto.getSnils()).ifPresent(entity -> {
            if (!entity.getId().equals(dto.getId()))
                throw new EntityNotUniqueException(Profile.class.getSimpleName(), "snils");
        });

        profileRepository.findByInn(dto.getInn()).ifPresent(entity -> {
            if (!entity.getId().equals(dto.getId()))
                throw new EntityNotUniqueException(Profile.class.getSimpleName(), "inn");
        });

        Profile profile = profileMapper.toEntity(dto);

        return profileMapper.toDto( profileRepository.save(profile) );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        profileRepository.findById(id).ifPresent(profileRepository::delete);
    }
}
