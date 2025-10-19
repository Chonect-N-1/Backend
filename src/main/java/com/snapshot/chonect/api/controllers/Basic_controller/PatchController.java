package com.snapshot.chonect.api.controllers.Basic_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface PatchController<RESPONSE, REQUEST> {
    @PatchMapping("/{id}")
    public ResponseEntity<RESPONSE> patch(@Validated @RequestBody REQUEST request, @PathVariable Long id);
}
