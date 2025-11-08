package com.snapshot.chonect.api.dto.response;

import com.snapshot.chonect.utils.enums.Role;
import com.snapshot.chonect.utils.enums.CustomerType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private CustomerType customerType;
    private String termsVersion;
    private String birthDate;
}
