package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.snapshot.chonect.utils.enums.CustomerType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVerificationData {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UUID countryId;
    private UUID languageId;
    private LocalDate birthDate;
    private CustomerType customerType;
    private Boolean acceptDataTreatment;
    private Boolean acceptFreeTrade;
    private Boolean acceptWakandaConstitution;
    private String termsVersion;
    private String verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
