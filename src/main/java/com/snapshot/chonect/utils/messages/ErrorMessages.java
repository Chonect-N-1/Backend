package com.snapshot.chonect.utils.messages;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ErrorMessages {
    public static String idNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el id suministrado";
        return message.formatted(entity);
    }
    
    public static String nameNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el nombre suministrado";
        return message.formatted(entity);
    }
    
    public static String emailNotFound(String entity) {
        final String message = "aqui no hay registros en la entidad %s con el email suministrado";
        return message.formatted(entity);
    }

    public static String cuentaNotVerificate(String entity) {
        final String message = "La cuenta del %s no se encuenta verificada.";
        return message.formatted(entity);
    }
    
    public static String verificationTimeExpired(String entity) {
        final String message = "El codigo de verificacion del %s esta caducado.";
        return message.formatted(entity);
    }
    
    public static String verificationCodeInvalid(String entity) {
        final String message = "El codigo de verificacion del %s es invalido.";
        return message.formatted(entity);
    }
    
    public static String alreadyVerificate(String entity) {
        final String message = "El %s ya esta verificado.";
        return message.formatted(entity);
    }
}