package com.chonect.backend.api.dto.auth;

public record AuthenticationResponse(
    String token,
    String username,
    String role
) {}
