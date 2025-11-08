package com.snapshot.chonect.infrastructure.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.LanguageEntity;
import com.snapshot.chonect.domain.repositories.LanguageRepository;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Transactional(readOnly = true)
    public List<LanguageEntity> getAll() {
        return languageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LanguageEntity getById(UUID id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Idioma no encontrado con id: " + id));
    }
}
