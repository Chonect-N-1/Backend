package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.CountryEntity;
import com.snapshot.chonect.domain.repositories.CountryRepository;

@Service
public class CountryService {

    @Autowired
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<CountryEntity> getAll() {
        return countryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CountryEntity getById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("País no encontrado con id: " + id));
    }
}
