package com.snapshot.chonect.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String search;

    @Size(min = 6, max = 100, message = "la contraseña debe tener entre 6 a 100 caracteres")
    @NotBlank(message = "la contraseña es requerida")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[\\d\\W]).{6,}$", message = "La contrasena tiene que tener al menos ~una letra mayuscula ~un caracter especial o numero ~almenos 6 caracteres.")
    private String password;
}
