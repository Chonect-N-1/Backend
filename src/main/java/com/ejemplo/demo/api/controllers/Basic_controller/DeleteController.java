package com.ejemplo.demo.api.controllers.Basic_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface DeleteController {
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id);
}