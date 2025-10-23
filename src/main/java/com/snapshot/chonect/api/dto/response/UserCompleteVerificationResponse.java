package com.snapshot.chonect.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCompleteVerificationResponse {
    private String message;
    private Long userId;
    private String username;
    private String email;
    private String token;
    private Long expiresIn;
}
