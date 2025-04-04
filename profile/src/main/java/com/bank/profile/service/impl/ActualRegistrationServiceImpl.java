package com.bank.profile.service.impl;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.mapper.RegistrationMapper;
import com.bank.profile.repository.ActualRegistrationRepository;
import com.bank.profile.service.ActualRegistrationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActualRegistrationServiceImpl implements ActualRegistrationService {
    private final ActualRegistrationRepository actualRegistrationRepository;
    private final RegistrationMapper registrationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDto> getAll() {
        return actualRegistrationRepository.findAll().stream().map(registrationMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationDto get(Long id) {
        return registrationMapper.toDto(actualRegistrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ActualRegistration.class.getSimpleName())));
    }

    @Override
    @Transactional
    public RegistrationDto create(RegistrationDto dto) {
        return registrationMapper.toDto(actualRegistrationRepository.save(registrationMapper.toActualRegistration(dto)));
    }

    @Override
    @Transactional
    public RegistrationDto update(RegistrationDto dto) {
        if (dto.getId() == null || actualRegistrationRepository.findById(dto.getId()).isEmpty())
            throw new EntityNotFoundException(ActualRegistration.class.getSimpleName());

        return registrationMapper.toDto(actualRegistrationRepository.save(registrationMapper.toActualRegistration(dto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        actualRegistrationRepository.findById(id).ifPresent(actualRegistrationRepository::delete);
    }
}
