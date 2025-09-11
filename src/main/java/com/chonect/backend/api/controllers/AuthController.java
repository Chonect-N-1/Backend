package com.chonect.backend.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chonect.backend.api.dto.auth.AuthenticationRequest;
import com.chonect.backend.api.dto.auth.AuthenticationResponse;
import com.chonect.backend.api.dto.request.UserCreateRequest;
import com.chonect.backend.domain.entities.UserEntity;
import com.chonect.backend.utils.enums.Role;
import com.chonect.backend.infrastructure.helpers.mappers.UserMapper;
import com.chonect.backend.security.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
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
            @Valid @RequestBody UserCreateRequest request) {
        // Por defecto, los usuarios registrados tendrán el rol CUSTOMER
        UserEntity user = userMapper.toUserCreateEntity(request);
        user.setRole(Role.CUSTOMER);
        return ResponseEntity.ok(authenticationService.register(user));
    }
}
