package com.snapshot.chonect.api.dto.response;

import com.snapshot.chonect.utils.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
