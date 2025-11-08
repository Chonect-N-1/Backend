package com.snapshot.chonect.api.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.snapshot.chonect.utils.enums.CustomerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPatchRequest {

    @Size(min = 1, max = 50, message = "el nombre de usuario debe tener entre 1 a 50 caracteres")
    private String username;

    @Size(min = 6, max = 100, message = "la contraseña debe tener entre 6 a 100 caracteres")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[\\d\\W]).{6,}$", message = "La contrasena tiene que tener al menos ~una letra mayuscula ~un caracter especial o numero ~almenos 6 caracteres.")
    private String password;

    @Size(min = 1, max = 100, message = "el email debe tener entre 1 a 100 caracteres")
    @Email(message = "el email no es valido")
    private String email;

    @Size(min = 1, max = 50, message = "el nombre debe tener entre 1 a 50 caracteres")
    private String firstName;

    @Size(min = 1, max = 50, message = "el apellido debe tener entre 1 a 50 caracteres")
    private String lastName;

    private UUID countryId;

    private UUID languageId;

    @Past(message = "la fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    private CustomerType customerType;
    private String termsVersion;

    // Método helper para verificar si un campo está presente y no es null
    public boolean hasUsername() {
        return username != null && !username.trim().isEmpty();
    }

    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean hasFirstName() {
        return firstName != null && !firstName.trim().isEmpty();
    }

    public boolean hasLastName() {
        return lastName != null && !lastName.trim().isEmpty();
    }

    public boolean hasCountryId() {
        return countryId != null;
    }

    public boolean hasLanguageId() {
        return languageId != null;
    }

    public boolean hasBirthDate() {
        return birthDate != null;
    }

    public boolean hasCustomerType() {
        return customerType != null;
    }

    public boolean hasTermsVersion() {
        return termsVersion != null && !termsVersion.trim().isEmpty();
    }
}
