package com.chonect.backend.api.dto.request;

import java.time.LocalDate;

import com.chonect.backend.api.validation.MinAge;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserCreateRequest {

    @Size(min = 1, max = 50, message = "el nombre de usuario debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el nombre de usuario es requerido")
    private String username;

    @Size(min = 6, max = 100, message = "la contraseña debe tener entre 6 a 100 caracteres")
    @NotBlank(message = "la contraseña es requerida")
    private String password;

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    private String email;

    @NotBlank(message = "el nombre completo es requerido")
    @Size(min = 1, max = 100, message = "el nombre completo debe tener entre 1 a 100 caracteres")
    private String fullName;

    @NotNull(message = "la fecha de nacimiento es requerida")
    @Past(message = "la fecha de nacimiento debe ser en el pasado")
    @MinAge(value = 15, message = "debes tener al menos 15 años para registrarte")
    private LocalDate birthDate;
}