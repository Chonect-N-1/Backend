package com.snapshot.chonect.api.dto.request;

import com.snapshot.chonect.utils.enums.CustomerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserVerificationUpdateRequest {

    @NotBlank(message = "el email es requerido")
    @Email(message = "el email no es valido")
    private String email;

    @NotNull(message = "el tipo de usuario es requerido")
    private CustomerType customerType;
}
