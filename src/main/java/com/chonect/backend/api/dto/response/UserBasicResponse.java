package com.chonect.backend.api.dto.response;

import com.chonect.backend.utils.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserBasicResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}