package com.snapshot.chonect.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.domain.models.LanguageEntity;
import com.snapshot.chonect.infrastructure.services.LanguageService;

@RestController
@RequestMapping(path = "/api/v1/languages")
public class LanguageController {

    @Autowired
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<List<LanguageEntity>> getAll() {
        return ResponseEntity.ok(languageService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LanguageEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(languageService.getById(id));
    }
}
