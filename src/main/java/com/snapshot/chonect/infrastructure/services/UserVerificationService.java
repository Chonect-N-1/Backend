package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.api.dto.request.UserVerificationRequest;
import com.snapshot.chonect.api.dto.request.UserVerificationUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserVerificationResponse;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.utils.VerificationCodeService;
import com.snapshot.chonect.utils.UserValidationService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.VerificationNotFoundException;
import com.snapshot.chonect.utils.exceptions.VerificationExpiredException;
import com.snapshot.chonect.utils.exceptions.InvalidVerificationCodeException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserVerificationService {

    private final EmailService emailService;

    private final VerificationCodeService verificationCodeService;

    private final UserValidationService userValidationService;

    private final UserServices userServices;

    // Cache temporal para datos de usuarios no verificados
    // En producción usar Redis
    private final Map<String, UserVerificationData> pendingVerifications = new ConcurrentHashMap<>();

    public UserVerificationResponse initiateVerification(UserVerificationRequest request) {
        // Validar credenciales de usuario
        userValidationService.validateUserCredentials(request.getEmail(), request.getUsername());

        // Generar código de verificación
        String verificationCode = verificationCodeService.generateVerificationCode();

        // Validar términos
        if (!Boolean.TRUE.equals(request.getAcceptDataTreatment()) ||
            !Boolean.TRUE.equals(request.getAcceptFreeTrade()) ||
            !Boolean.TRUE.equals(request.getAcceptWakandaConstitution())) {
            throw new BadRequestException("Debes aceptar todos los términos y condiciones");
        }

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
                .customerType(request.getCustomerType())
                .acceptDataTreatment(request.getAcceptDataTreatment())
                .acceptFreeTrade(request.getAcceptFreeTrade())
                .acceptWakandaConstitution(request.getAcceptWakandaConstitution())
                .termsVersion(request.getTermsVersion())
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        // Guardar en cache temporal
        pendingVerifications.put(request.getEmail(), verificationData);

        // Enviar email con código de verificación
        emailService.sendVerificationEmail(request.getEmail(), request.getFirstName(), verificationCode);

        return UserVerificationResponse.builder()
                .message("Datos recibidos. Revisa tu email para verificar la cuenta.")
                .status("pending")
                .email(request.getEmail())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Transactional
    public UserEntity completeVerification(String email, String verificationCode) {
        // Buscar datos de verificación
        UserVerificationData verificationData = pendingVerifications.get(email);

        if (verificationData == null) {
            throw new VerificationNotFoundException("No se encontraron datos de verificación para este email");
        }

        // Verificar que no haya expirado
        if (LocalDateTime.now().isAfter(verificationData.getExpiresAt())) {
            pendingVerifications.remove(email);
            throw new VerificationExpiredException("El código de verificación ha expirado");
        }

        // Verificar código
        if (!verificationData.getVerificationCode().equals(verificationCode)) {
            throw new InvalidVerificationCodeException("Código de verificación inválido");
        }

        // Crear usuario real en base de datos usando el método helper
        UserServices.UserCreationData creationData = UserServices.UserCreationData.builder()
                .username(verificationData.getUsername())
                .email(verificationData.getEmail())
                .password(verificationData.getPassword())
                .firstName(verificationData.getFirstName())
                .lastName(verificationData.getLastName())
                .countryId(verificationData.getCountryId())
                .languageId(verificationData.getLanguageId())
                .birthDate(verificationData.getBirthDate())
                .enabled(true)
                .role(com.snapshot.chonect.utils.enums.Role.CUSTOMER)
                .customerType(verificationData.getCustomerType())
                .termsVersion(verificationData.getTermsVersion())
                .verificationCode(null)
                .verificationCodeExpireAt(null)
                .build();
        UserEntity savedUser = userServices.createUserFromData(creationData);

        // Remover datos temporales
        pendingVerifications.remove(email);

        // Retornar usuario creado
        return savedUser;
    }

    public void updateVerificationData(UserVerificationUpdateRequest request) {
        UserVerificationData verificationData = pendingVerifications.get(request.getEmail());

        if (verificationData == null) {
            throw new VerificationNotFoundException("No se encontraron datos de verificación para este email");
        }

        // Actualizar el tipo de usuario
        verificationData.setCustomerType(request.getCustomerType());

        // Guardar los cambios
        pendingVerifications.put(request.getEmail(), verificationData);
    }

    // Método para limpiar verificaciones expiradas
    public void cleanupExpiredVerifications() {
        LocalDateTime now = LocalDateTime.now();
        pendingVerifications.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpiresAt()));
    }
}
