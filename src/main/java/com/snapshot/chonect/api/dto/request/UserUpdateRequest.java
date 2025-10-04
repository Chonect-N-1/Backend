package com.snapshot.chonect.api.dto.request;

import com.snapshot.chonect.utils.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserUpdateRequest extends UserRequest {
    @NotNull(message = "el rol es requerido")
    private Role role;
}