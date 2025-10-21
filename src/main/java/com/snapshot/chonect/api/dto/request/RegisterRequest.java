package com.snapshot.chonect.api.dto.request;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RegisterRequest {
    @Size(min = 1, max = 50, message = "el nombre de usuario debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el nombre de usuario es requerido")
    private String username;

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    @UniqueElements(message = "El email tiene que ser unico")
    private String email;

    @Size(min = 6, max = 100, message = "la contraseña debe tener entre 6 a 100 caracteres")
    @NotBlank(message = "la contraseña es requerida")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[\\d\\W]).{6,}$", message = "La contrasena tiene que tener al menos ~una letra mayuscula ~un caracter especial o numero ~almenos 6 caracteres.")
    private String password;

    @Size(min = 1, max = 50, message = "el nombre debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el nombre es requerido")
    private String firstName;

    @Size(min = 1, max = 50, message = "el apellido debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el apellido es requerido")
    private String lastName;

}
