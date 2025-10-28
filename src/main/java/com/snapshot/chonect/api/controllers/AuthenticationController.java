package com.snapshot.chonect.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RequestMapping(path = "/api/v1/auth")
@RestController
@AllArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserVerificationService userVerificationService;



    @PostMapping("/initiate-verification")
    @Operation(summary = "Iniciar verificación de usuario", description = "Inicia el proceso de verificación enviando código por email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código de verificación enviado exitosamente",
                content = @Content(schema = @Schema(implementation = UserVerificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de verificación inválidos"),
        @ApiResponse(responseCode = "409", description = "El email o username ya está registrado")
    })
    public ResponseEntity<UserVerificationResponse> initiateVerification(@RequestBody UserVerificationRequest request) {
        UserVerificationResponse response = userVerificationService.initiateVerification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-verification")
    @Operation(summary = "Actualizar datos de verificación", description = "Actualiza el tipo de usuario en los datos temporales de verificación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos actualizados exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron datos de verificación para este email")
    })
    public ResponseEntity<String> updateVerification(@RequestBody UserVerificationUpdateRequest request) {
        userVerificationService.updateVerificationData(request);
        return ResponseEntity.ok("Datos de verificación actualizados exitosamente para el email: " + request.getEmail());
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwt = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwt, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }



    @PostMapping("/resend")
    @Operation(summary = "Reenviar código de verificación", description = "Reenvía el código de verificación al email del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código reenviado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al reenviar código"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email){
        authenticationService.resendVerificationCode(email);
        return ResponseEntity.ok("Se ha reenviado el código de verificación al correo: " + email + "!!");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente la cuenta de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<String> deleteUser(@RequestParam String email) {
        // Llama al nuevo método del servicio
        authenticationService.deleteUserByEmail(email);
        // Retorna una respuesta HTTP 200 OK con un mensaje de éxito
        return ResponseEntity.ok("Usuario con email: " + email + " eliminado correctamente.");
    }

    @PostMapping("/verify-user")
    @Operation(summary = "Completar verificación de usuario", description = "Completa la verificación de un usuario creado con PATCH y devuelve token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario verificado y cuenta creada exitosamente",
                content = @Content(schema = @Schema(implementation = UserCompleteVerificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Código de verificación inválido o expirado"),
        @ApiResponse(responseCode = "404", description = "No se encontraron datos de verificación")
    })
    public ResponseEntity<UserCompleteVerificationResponse> verifyUserAccount(@RequestBody UserCompleteVerificationRequest request) {
        logger.info("Starting user verification for email: {}", request.getEmail());
        // Completar verificación y crear usuario real
        UserEntity newUser = userVerificationService.completeVerification(request.getEmail(), request.getVerificationCode());
        logger.info("User created successfully with ID: {}", newUser.getId());

        // Generar token JWT para el usuario recién creado
        String jwt = jwtService.generateToken(newUser);
        logger.info("JWT token generated successfully for user: {}", newUser.getUsername());
        // Crear respuesta completa
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
}
