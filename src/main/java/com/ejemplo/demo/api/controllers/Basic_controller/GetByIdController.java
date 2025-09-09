package com.ejemplo.demo.api.controllers.Basic_controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public interface GetByIdController<RESPONSE> {
    @GetMapping("/{id}")
    public ResponseEntity<RESPONSE> getById(@PathVariable Long id);
}