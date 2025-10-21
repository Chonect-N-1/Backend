package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.LanguageEntity;
import com.snapshot.chonect.domain.repositories.LanguageRepository;

@Service
public class LanguageService {

    @Autowired
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Transactional(readOnly = true)
    public List<LanguageEntity> getAll() {
        return languageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LanguageEntity getById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idioma no encontrado con id: " + id));
    }
}
