package com.snapshot.chonect.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.domain.models.CountryEntity;
import com.snapshot.chonect.infrastructure.services.CountryService;

@RestController
@RequestMapping(path = "/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public ResponseEntity<List<CountryEntity>> getAll() {
        return ResponseEntity.ok(countryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getById(id));
    }
}
