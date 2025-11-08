package com.snapshot.chonect.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.api.dto.response.CountryResponse;
import com.snapshot.chonect.api.dto.response.LanguageResponse;
import com.snapshot.chonect.infrastructure.services.CountryService;
import com.snapshot.chonect.infrastructure.services.LanguageService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/reference-data")
// Endpoint temporal para ayudar con la migración UUID - remover después
public class ReferenceDataController {

    private final CountryService countryService;
    private final LanguageService languageService;

    public ReferenceDataController(
            CountryService countryService,
            LanguageService languageService) {
        this.countryService = countryService;
        this.languageService = languageService;
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getCountriesForUuidMigration() {
        return ResponseEntity.ok(countryService.getAll().stream()
                .map(country -> CountryResponse.builder()
                        .id(country.getId())
                        .name(country.getName())
                        .code(country.getCode())
                        .build())
                .toList());
    }

    @GetMapping("/languages")
    public ResponseEntity<List<LanguageResponse>> getLanguagesForUuidMigration() {
        return ResponseEntity.ok(languageService.getAll().stream()
                .map(language -> LanguageResponse.builder()
                        .id(language.getId())
                        .name(language.getName())
                        .code(language.getCode())
                        .build())
                .toList());
    }
}
