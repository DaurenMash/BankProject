package com.bank.profile.service.impl;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.ProfileDto;
import com.bank.profile.entity.AccountDetails;
import com.bank.profile.mapper.AccountDetailsMapper;
import com.bank.profile.repository.AccountDetailsRepository;
import com.bank.profile.service.AccountDetailsService;
import com.bank.profile.service.ProfileService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDetailsServiceImpl implements AccountDetailsService {

    private final AccountDetailsRepository accountDetailsRepository;
    private final AccountDetailsMapper accountDetailsMapper;
    private final ProfileService profileService;

    @Override
    @Transactional(readOnly = true)
    public List<AccountDetailsDto> getAll() {
        return accountDetailsRepository.findAll()
                .stream().map(accountDetailsMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDetailsDto get(Long id) {
        return accountDetailsMapper.toDto(
                accountDetailsRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(AccountDetails.class.getSimpleName())));
    }

    @Override
    @Transactional
    public AccountDetailsDto create(AccountDetailsDto dto) {
        if (dto.getId() != null && accountDetailsRepository.findById(dto.getId()).isPresent())
            throw new EntityExistsException(AccountDetails.class.getSimpleName());

        ProfileDto profile = profileService.createEmpty();
        dto.setProfileId(profile.getId());

        return accountDetailsMapper
                .toDto(accountDetailsRepository
                        .save(accountDetailsMapper
                                .toEntity(dto)));
    }

    @Override
    @Transactional
    public AccountDetailsDto update(AccountDetailsDto dto) {
        if (dto.getId() == null || accountDetailsRepository.findById(dto.getId()).isEmpty())
            throw new EntityNotFoundException(AccountDetails.class.getSimpleName());

        return accountDetailsMapper
                .toDto(accountDetailsRepository
                        .save(accountDetailsMapper
                                .toEntity(dto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        accountDetailsRepository.findById(id)
                .ifPresent(accountDetailsRepository::delete);
    }
}
