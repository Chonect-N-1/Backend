package com.snapshot.chonect.api.controllers.basic_controller;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public interface GetByIdController<T, I> {
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable @NonNull I id);
}
