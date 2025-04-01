package com.bank.profile.service.impl;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.mapper.RegistrationMapper;
import com.bank.profile.repository.ActualRegistrationRepository;
import com.bank.profile.service.ActualRegistrationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActualRegistrationServiceImpl implements ActualRegistrationService {
    private final ActualRegistrationRepository actualRegistrationRepository;
    private final RegistrationMapper registrationMapper;

    public ActualRegistrationServiceImpl(ActualRegistrationRepository actualRegistrationRepository, RegistrationMapper registrationMapper) {
        this.actualRegistrationRepository = actualRegistrationRepository;
        this.registrationMapper = registrationMapper;
    }

    @Override
    public List<RegistrationDto> getAll() {
        return actualRegistrationRepository.findAll().stream().map(registrationMapper::toDto).toList();
    }

    @Override
    public RegistrationDto get(Long id) {
        return registrationMapper.toDto(actualRegistrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ActualRegistration.class.getSimpleName())));
    }

    @Override
    public RegistrationDto create(RegistrationDto dto) {
        return registrationMapper.toDto(actualRegistrationRepository.save(registrationMapper.toActualRegistration(dto)));
    }

    @Override
    public RegistrationDto update(RegistrationDto dto) {
        if (dto.getId() == null || actualRegistrationRepository.findById(dto.getId()).isEmpty())
            throw new EntityNotFoundException(ActualRegistration.class.getSimpleName());

        return registrationMapper.toDto(actualRegistrationRepository.save(registrationMapper.toActualRegistration(dto)));
    }

    @Override
    public void delete(Long id) {
        actualRegistrationRepository.findById(id).ifPresent(actualRegistrationRepository::delete);
    }
}
