package com.snapshot.chonect.api.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.snapshot.chonect.api.validation.MinAge;
import com.snapshot.chonect.utils.enums.CustomerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserVerificationRequest {

    @Size(min = 1, max = 50, message = "el nombre de usuario debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el nombre de usuario es requerido")
    private String username;

    @Size(min = 6, max = 100, message = "la contraseña debe tener entre 6 a 100 caracteres")
    @NotBlank(message = "la contraseña es requerida")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[\\d\\W]).{6,}$", message = "La contrasena tiene que tener al menos ~una letra mayuscula ~un caracter especial o numero ~almenos 6 caracteres.")
    private String password;

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    private String email;

    @Size(min = 1, max = 50, message = "el nombre debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el nombre es requerido")
    private String firstName;

    @Size(min = 1, max = 50, message = "el apellido debe tener entre 1 a 50 caracteres")
    @NotBlank(message = "el apellido es requerido")
    private String lastName;

    @NotNull(message = "el país es requerido")
    private UUID countryId;

    @NotNull(message = "el idioma es requerido")
    private UUID languageId;

    @NotNull(message = "la fecha de nacimiento es requerida")
    @MinAge(16)
    private LocalDate birthDate;

    @NotNull(message = "el tipo de usuario es requerido")
    private CustomerType customerType;

    @NotNull(message = "debes aceptar el tratado de datos personales")
    private Boolean acceptDataTreatment;

    @NotNull(message = "debes aceptar el tratado de libre comercio")
    private Boolean acceptFreeTrade;

    @NotNull(message = "debes aceptar el tratado de wakanda de la constitución 12921 de marvel")
    private Boolean acceptWakandaConstitution;

    @NotNull(message = "la versión de términos es requerida")
    private String termsVersion;
}
