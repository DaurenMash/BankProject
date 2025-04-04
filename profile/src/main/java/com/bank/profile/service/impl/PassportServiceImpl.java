package com.bank.profile.service.impl;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Registration;
import com.bank.profile.mapper.PassportMapper;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.RegistrationRepository;
import com.bank.profile.service.PassportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassportServiceImpl implements PassportService {
    private final PassportRepository passportRepository;
    private final PassportMapper passportMapper;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PassportDto> getAll() {
        return passportRepository.findAll().stream().map(passportMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassportDto get(Long id) {
        return passportMapper.toDto(passportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Passport.class.getSimpleName())));
    }

    @Override
    @Transactional
    public PassportDto create(PassportDto dto) {
        Passport passport = passportMapper.toEntity(dto);
        if (
                passport.getRegistration() == null ||
                passport.getRegistration().getId() == null ||
                registrationRepository.findById(passport.getRegistration().getId()).isEmpty()
        ) {
            throw new EntityNotFoundException(Registration.class.getSimpleName());
        }

        return passportMapper.toDto(passportRepository.save(passport));
    }

    @Override
    @Transactional
    public PassportDto update(PassportDto dto) {
        if (passportRepository.findById(dto.getId()).isEmpty())
            throw new EntityNotFoundException(Passport.class.getSimpleName());

        Passport passport = passportMapper.toEntity(dto);

        if (
                passport.getRegistration() == null ||
                passport.getRegistration().getId() == null ||
                registrationRepository.findById(passport.getRegistration().getId()).isEmpty()
        ) {
            throw new EntityNotFoundException(Registration.class.getSimpleName());
        }

        return passportMapper.toDto(passportRepository.save(passportMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        passportRepository.findById(id).ifPresent(passportRepository::delete);
    }
}
