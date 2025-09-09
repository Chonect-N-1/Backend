package com.ejemplo.demo.api.dto.auth;

public record AuthenticationResponse(
    String token,
    String username,
    String role
) {}
