package com.snapshot.chonect.api.error_handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.snapshot.chonect.api.dto.errors.BaseErrorResponse;
import com.snapshot.chonect.api.dto.errors.ErrorsResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class SecurityErrorController {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseErrorResponse handleBadCredentials(BadCredentialsException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("Usuario o contraseña incorrectos");
        
        return ErrorsResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseErrorResponse handleAccountStatus(AccountStatusException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("La cuenta está bloqueada o deshabilitada");
        
        return ErrorsResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseErrorResponse handleAccessDenied(AccessDeniedException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("No tienes permisos para acceder a este recurso");
        
        return ErrorsResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseErrorResponse handleSignatureException(SignatureException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("Token JWT inválido o firma no verificada");
        
        return ErrorsResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseErrorResponse handleExpiredJwt(ExpiredJwtException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("El token JWT ha expirado");
        
        return ErrorsResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseErrorResponse handleAuthentication(AuthenticationException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("Error de autenticación: " + exception.getMessage());
        
        return ErrorsResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.name())
                .errors(errors)
                .build();
    }
}