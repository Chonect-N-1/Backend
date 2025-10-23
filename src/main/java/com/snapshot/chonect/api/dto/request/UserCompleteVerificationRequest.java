package com.snapshot.chonect.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserCompleteVerificationRequest {

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    private String email;

    @Size(min = 6, max = 6, message = "el código de verificación debe tener 6 caracteres")
    @NotBlank(message = "el código de verificación es requerido")
    private String verificationCode;
}
