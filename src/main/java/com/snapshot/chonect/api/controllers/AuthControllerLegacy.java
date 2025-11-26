package com.snapshot.chonect.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.snapshot.chonect.api.dto.request.LoginUserDto;
import com.snapshot.chonect.api.dto.request.UserCompleteVerificationRequest;
import com.snapshot.chonect.api.dto.request.UserVerificationRequest;
import com.snapshot.chonect.api.dto.request.UserVerificationUpdateRequest;
import com.snapshot.chonect.api.dto.response.LoginResponse;
import com.snapshot.chonect.api.dto.response.UserCompleteVerificationResponse;
import com.snapshot.chonect.api.dto.response.UserVerificationResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.infrastructure.services.AuthenticationService;
import com.snapshot.chonect.infrastructure.services.JwtService;
import com.snapshot.chonect.infrastructure.services.UserVerificationService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;

/**
 * Controlador legacy para soporte de rutas /auth/* (sin prefijo /api/v1)
 * Este controlador mantiene compatibilidad con frontends que usan las rutas
 * antiguas
 */
@RequestMapping(path = "/auth")
@RestController
@AllArgsConstructor
@Hidden // Ocultar en Swagger para no duplicar documentación
public class AuthControllerLegacy {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserVerificationService userVerificationService;

    @PostMapping("/initiate-verification")
    public ResponseEntity<UserVerificationResponse> initiateVerification(
            @Validated @RequestBody UserVerificationRequest request) {
        UserVerificationResponse response = userVerificationService.initiateVerification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-verification")
    public ResponseEntity<String> updateVerification(@Validated @RequestBody UserVerificationUpdateRequest request) {
        userVerificationService.updateVerificationData(request);
        return ResponseEntity
                .ok("Datos de verificación actualizados exitosamente para el email: " + request.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Validated @RequestBody LoginUserDto loginUserDto) {
        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwt = jwtService.generateTokenWithUserInfo(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwt, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationCode(@RequestParam @NotBlank @Email String email) {
        authenticationService.resendVerificationCode(email);
        return ResponseEntity.ok("Se ha reenviado el código de verificación al correo: " + email + "!!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam @NotBlank @Email String email) {
        authenticationService.deleteUserByEmail(email);
        return ResponseEntity.ok("Usuario con email: " + email + " eliminado correctamente.");
    }

    @PostMapping("/verify-user")
    public ResponseEntity<UserCompleteVerificationResponse> verifyUserAccount(
            @Validated @RequestBody UserCompleteVerificationRequest request) {
        UserEntity newUser = userVerificationService.completeVerification(request.getEmail(),
                request.getVerificationCode());
        String jwt = jwtService.generateTokenWithUserInfo(newUser);

        UserCompleteVerificationResponse response = UserCompleteVerificationResponse.builder()
                .message("Cuenta verificada y creada exitosamente")
                .userId(newUser.getId())
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .token(jwt)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserVerificationResponse> signup(@Validated @RequestBody UserVerificationRequest request) {
        UserVerificationResponse response = userVerificationService.initiateVerification(request);
        return ResponseEntity.ok(response);
    }
}
