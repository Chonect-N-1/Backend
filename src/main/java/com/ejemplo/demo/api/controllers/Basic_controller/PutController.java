package com.ejemplo.demo.api.controllers.Basic_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface PutController<RESPONSE, REQUEST> {
    @PutMapping("/{id}")
    public ResponseEntity<RESPONSE> update(@Validated @RequestBody REQUEST request, @PathVariable Long id);
}