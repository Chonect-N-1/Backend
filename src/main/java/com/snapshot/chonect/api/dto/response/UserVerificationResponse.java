package com.snapshot.chonect.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVerificationResponse {
    private String message;
    private String status;
    private String email;
    private Long timestamp;
}
