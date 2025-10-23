package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.api.dto.request.UserVerificationRequest;
import com.snapshot.chonect.api.dto.response.UserVerificationResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.utils.VerificationCodeService;
import com.snapshot.chonect.utils.UserValidationService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserVerificationService {

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final VerificationCodeService verificationCodeService;

    @Autowired
    private final UserValidationService userValidationService;

    @Autowired
    private final UserServices userServices;

    // Cache temporal para datos de usuarios no verificados
    // En producción usar Redis
    private final Map<String, UserVerificationData> pendingVerifications = new ConcurrentHashMap<>();

    public UserVerificationResponse initiateVerification(UserVerificationRequest request) {
        // Validar credenciales de usuario
        userValidationService.validateUserCredentials(request.getEmail(), request.getUsername());

        // Generar código de verificación
        String verificationCode = verificationCodeService.generateVerificationCode();

        // Crear datos de verificación
        UserVerificationData verificationData = UserVerificationData.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .countryId(request.getCountryId())
                .languageId(request.getLanguageId())
                .birthDate(request.getBirthDate())
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        // Guardar en cache temporal
        pendingVerifications.put(request.getEmail(), verificationData);

        // Enviar email con código de verificación
        try {
            emailService.sendVerificationEmail(request.getEmail(), request.getFirstName(), verificationCode);
        } catch (BadRequestException e) {
            throw e;
        }

        return UserVerificationResponse.builder()
                .message("Datos recibidos. Revisa tu email para verificar la cuenta.")
                .status("pending")
                .email(request.getEmail())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public UserEntity completeVerification(String email, String verificationCode) {
        // Buscar datos de verificación
        UserVerificationData verificationData = pendingVerifications.get(email);

        if (verificationData == null) {
            throw new RuntimeException("No se encontraron datos de verificación para este email");
        }

        // Verificar que no haya expirado
        if (LocalDateTime.now().isAfter(verificationData.getExpiresAt())) {
            pendingVerifications.remove(email);
            throw new RuntimeException("El código de verificación ha expirado");
        }

        // Verificar código
        if (!verificationData.getVerificationCode().equals(verificationCode)) {
            throw new RuntimeException("Código de verificación inválido");
        }

        // Crear usuario real en base de datos usando el método helper
        UserEntity savedUser = userServices.createUserFromData(
                verificationData.getUsername(),
                verificationData.getEmail(),
                verificationData.getPassword(),
                verificationData.getFirstName(),
                verificationData.getLastName(),
                verificationData.getCountryId(),
                verificationData.getLanguageId(),
                verificationData.getBirthDate() != null ? verificationData.getBirthDate().toString() : null,
                true,
                com.snapshot.chonect.utils.enums.Role.CUSTOMER,
                null,
                null
        );

        // Remover datos temporales
        pendingVerifications.remove(email);

        // Retornar usuario creado
        return savedUser;
    }

    // Método para limpiar verificaciones expiradas
    public void cleanupExpiredVerifications() {
        LocalDateTime now = LocalDateTime.now();
        pendingVerifications.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpiresAt()));
    }
}
