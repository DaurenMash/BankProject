package com.bank.profile.service.impl;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Registration;
import com.bank.profile.mapper.RegistrationMapper;
import com.bank.profile.repository.RegistrationRepository;
import com.bank.profile.service.RegistrationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;

    @Override
    public List<RegistrationDto> getAll() {
        return registrationRepository.findAll().stream().map(registrationMapper::toDto).toList();
    }

    @Override
    public RegistrationDto get(Long id) {
        return registrationMapper.toDto(registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Registration.class.getSimpleName())));
    }

    @Override
    public RegistrationDto create(RegistrationDto dto) {
        return registrationMapper.toDto(registrationRepository.save(registrationMapper.toRegistration(dto)));
    }

    @Override
    public RegistrationDto update(RegistrationDto dto) {
        if (dto.getId() == null || registrationRepository.findById(dto.getId()).isEmpty())
            throw new EntityNotFoundException(Registration.class.getSimpleName());

        return registrationMapper.toDto(registrationRepository.save(registrationMapper.toRegistration(dto)));
    }

    @Override
    public void delete(Long id) {
        registrationRepository.findById(id).ifPresent(registrationRepository::delete);
    }
}
