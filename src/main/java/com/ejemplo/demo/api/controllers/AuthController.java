package com.ejemplo.demo.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejemplo.demo.api.dto.auth.AuthenticationRequest;
import com.ejemplo.demo.api.dto.auth.AuthenticationResponse;
import com.ejemplo.demo.domain.entities.UserEntity;
import com.ejemplo.demo.utils.enums.Role;
import com.ejemplo.demo.infrastructure.helpers.mappers.UserMapper;
import com.ejemplo.demo.security.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth") // ✅ ahora sí coincide con tu frontend
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody AuthenticationRequest request) {
        // Por defecto, los usuarios registrados tendrán el rol CUSTOMER
        UserEntity user = userMapper.toUserCreateEntity(request);
        user.setRole(Role.CUSTOMER);
        return ResponseEntity.ok(authenticationService.register(user));
    }
}
