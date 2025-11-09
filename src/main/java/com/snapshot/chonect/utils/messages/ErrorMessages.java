package com.snapshot.chonect.utils.messages;

import org.springframework.lang.NonNull;

import java.util.Objects;

public class ErrorMessages {
    private ErrorMessages() {
        // Prevent instantiation
    }
    public static @NonNull String idNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el id suministrado";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String nameNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el nombre suministrado";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String emailNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el email suministrado";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String cuentaNotVerificate(String entity) {
        final String message = "La cuenta del %s no se encuenta verificada.";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String verificationTimeExpired(String entity) {
        final String message = "El codigo de verificacion del %s esta caducado.";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String verificationCodeInvalid(String entity) {
        final String message = "El codigo de verificacion del %s es invalido.";
        return Objects.requireNonNull(message.formatted(entity));
    }

    public static @NonNull String alreadyVerificate(String entity) {
        final String message = "El %s ya esta verificado.";
        return Objects.requireNonNull(message.formatted(entity));
    }
}
