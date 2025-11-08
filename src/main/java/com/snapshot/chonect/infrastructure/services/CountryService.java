package com.snapshot.chonect.infrastructure.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.CountryEntity;
import com.snapshot.chonect.domain.repositories.CountryRepository;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<CountryEntity> getAll() {
        return countryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CountryEntity getById(UUID id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("País no encontrado con id: " + id));
    }
}
