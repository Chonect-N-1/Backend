package com.chonect.backend.api.controllers.Basic_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface PostController<RESPONSE, REQUEST> {
    @PostMapping
    public ResponseEntity<RESPONSE> insert(@Validated @RequestBody REQUEST request);
}