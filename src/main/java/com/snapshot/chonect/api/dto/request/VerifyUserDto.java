package com.snapshot.chonect.api.dto.request;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDto {
    private String search;

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    @UniqueElements(message = "El email tiene que ser unico")
    private String email;

    @Size(min = 6, max = 6, message = "El campo debe tener exactamente 6 caracteres.")
    private String verificationCode;
}
