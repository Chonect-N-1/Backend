package com.snapshot.chonect.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.api.dto.request.LoginUserDto;
import com.snapshot.chonect.api.dto.request.RegisterRequest;
import com.snapshot.chonect.api.dto.request.VerifyUserDto;
import com.snapshot.chonect.api.dto.response.LoginResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.infrastructure.services.AuthenticationService;
import com.snapshot.chonect.infrastructure.services.JwtService;
import com.snapshot.chonect.infrastructure.services.UserServices;

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
    
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserServices userServices;

    @PostMapping("/signup")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = UserEntity.class))),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    public ResponseEntity<UserEntity> register(@RequestBody RegisterRequest registerUserDto){
        UserEntity registerUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registerUser);
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

    @PostMapping("/verify")
    @Operation(summary = "Verificar cuenta", description = "Verifica la cuenta de usuario mediante código de verificación y devuelve token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta verificada exitosamente",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Código de verificación inválido")
    })
    public ResponseEntity<LoginResponse> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);

            // Obtener el usuario verificado para generar el token
            UserEntity verifiedUser = userServices.getByEmail(verifyUserDto.getEmail());

            // Generar token JWT
            String jwt = jwtService.generateToken(verifiedUser);
            LoginResponse loginResponse = new LoginResponse(jwt, jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/resend")
    @Operation(summary = "Reenviar código de verificación", description = "Reenvía el código de verificación al email del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código reenviado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al reenviar código")
    })
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Se a re enviado el codigo de verificacion al correo: " + email + "!!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Se produjo el error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente la cuenta de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al eliminar usuario")
    })
    public ResponseEntity<String> deleteUser(@RequestParam String email) {
        try {
            // Llama al nuevo método del servicio
            authenticationService.deleteUserByEmail(email);
            // Retorna una respuesta HTTP 200 OK con un mensaje de éxito
            return ResponseEntity.ok("Usuario con email: " + email + " eliminado correctamente.");
        } catch (Exception e) {
            // Si el servicio lanza una excepción (ej. usuario no encontrado), se captura aquí
            return ResponseEntity.badRequest().body("Se produjo el error: " + e.getMessage());
        }
    }
}
