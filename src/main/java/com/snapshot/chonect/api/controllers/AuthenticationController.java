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

import lombok.AllArgsConstructor;

@RequestMapping(path = "/api/v1/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterRequest registerUserDto){
        UserEntity registerUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwt = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwt, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Se a verificado la cuenta del usuario con email: " + verifyUserDto.getEmail() +"!!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Se produjo el error: " + e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Se a re enviado el codigo de verificacion al correo: " + email + "!!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Se produjo el error: " + e.getMessage());
        }
    }

    // debo cambiar esto por delete
    @DeleteMapping("/delete")
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
