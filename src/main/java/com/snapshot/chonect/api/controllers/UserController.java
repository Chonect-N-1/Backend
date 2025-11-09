package com.snapshot.chonect.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snapshot.chonect.api.controllers.basic_controller.GetByIdController;
import com.snapshot.chonect.api.dto.request.UserPatchRequest;
import com.snapshot.chonect.api.dto.request.UserVerificationRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.api.dto.response.UserVerificationResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.infrastructure.services.JwtService;
import com.snapshot.chonect.infrastructure.services.UserServices;
import com.snapshot.chonect.infrastructure.services.UserVerificationService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import java.util.UUID;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/user")
@AllArgsConstructor
public class UserController implements GetByIdController<UserResponse, UUID>
    {

    private final UserServices userServices;

    private final JwtService jwtService;

    private final UserVerificationService userVerificationService;

    @Override
    @NonNull
    public ResponseEntity<UserResponse> getById(@NonNull UUID id) {
        return ResponseEntity.ok(this.userServices.getById(id));
    }

    // Crear nuevo usuario con verificación
    @PostMapping
    @NonNull
    public ResponseEntity<UserVerificationResponse> createUser(@Validated @NonNull @RequestBody UserVerificationRequest request) {
        UserVerificationResponse response = userVerificationService.initiateVerification(request);
        return ResponseEntity.ok(response);
    }

    // Actualizar usuario existente (requiere autenticación)
    @PatchMapping("/{id}")
    @NonNull
    public ResponseEntity<UserResponse> updateUser(@Validated @NonNull @RequestBody UserPatchRequest request, @PathVariable @NonNull UUID id, @RequestHeader("Authorization") @NonNull String token) {
        // Extraer el usuario del token JWT
        String jwtToken = token.replace("Bearer ", "");
        String username = java.util.Objects.requireNonNull(jwtService.extractUsername(jwtToken), "Username cannot be null");

        // Obtener el usuario autenticado
        UserEntity authenticatedUser = userServices.getByEmail(username);

        // Verificar que el usuario solo pueda modificar su propia cuenta
        if (!authenticatedUser.getId().equals(id)) {
            throw new BadRequestException("No tienes permisos para modificar esta cuenta");
        }

        return ResponseEntity.ok(this.userServices.patch(request, id, authenticatedUser));
    }
}
