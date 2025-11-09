package com.snapshot.chonect.infrastructure.services;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import com.snapshot.chonect.api.dto.request.UserPatchRequest;
import com.snapshot.chonect.api.dto.request.UserUpdateRequest;
import com.snapshot.chonect.api.dto.response.UserResponse;
import com.snapshot.chonect.domain.models.CountryEntity;
import com.snapshot.chonect.domain.models.LanguageEntity;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.enums.CustomerType;
import com.snapshot.chonect.infrastructure.abstract_services.IUserService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;
import com.snapshot.chonect.infrastructure.helpers.UserMappers;
import com.snapshot.chonect.utils.VerificationCodeService;
import com.snapshot.chonect.utils.UserValidationService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserServices implements IUserService {

    private final UserMappers userMapper;

    private final UserRepository userRepository;

    private final SupportService<UserEntity, UUID> supportService;

    private final CountryService countryService;

    private final LanguageService languageService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final VerificationCodeService verificationCodeService;

    private final UserValidationService userValidationService;

    @Override
    public @NonNull UserResponse getById(@NonNull UUID id) {
        UserEntity userEntity = java.util.Objects.requireNonNull(this.supportService.findById(userRepository, id, "UserEntity"));
        return java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(userEntity));
    }

    @Override
    public @NonNull UserResponse update(UserUpdateRequest userRequest, UUID id) {
        UserEntity userUpdate = java.util.Objects.requireNonNull(this.userMapper.requestUpdateToEntity(userRequest));
        userUpdate.setId(id);
        return java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(java.util.Objects.requireNonNull(this.userRepository.save(userUpdate))));
    }

    public @NonNull UserResponse patch(@NonNull UserPatchRequest request, @NonNull UUID id) {
        return patch(request, id, null);
    }

    public @NonNull UserResponse patch(@NonNull UserPatchRequest request, @NonNull UUID id, UserEntity authenticatedUser) {
        UserEntity existingUser = java.util.Objects.requireNonNull(this.supportService.findById(userRepository, id, "UserEntity"));

        // Si hay usuario autenticado, verificar que solo modifique su propia cuenta
        if (authenticatedUser != null && !authenticatedUser.getId().equals(id)) {
            throw new UnauthorizedException("No tienes permisos para modificar esta cuenta");
        }

        // Actualización parcial - solo campos presentes y no vacíos
        if (request.hasUsername()) {
            existingUser.setUsername(request.getUsername());
        }
        if (request.hasPassword()) {
            existingUser.setPassword(request.getPassword());
        }
        if (request.hasEmail()) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.hasFirstName()) {
            existingUser.setFirstName(request.getFirstName());
        }
        if (request.hasLastName()) {
            existingUser.setLastName(request.getLastName());
        }
        if (request.hasCountryId()) {
            validateAndSetCountry(existingUser, request.getCountryId());
        }
        if (request.hasLanguageId()) {
            validateAndSetLanguage(existingUser, request.getLanguageId());
        }
        if (request.hasBirthDate()) {
            existingUser.setBirthDate(request.getBirthDate());
        }
        if (request.hasCustomerType()) {
            existingUser.setCustomerType(request.getCustomerType());
        }
        if (request.hasTermsVersion()) {
            existingUser.setTermsVersion(request.getTermsVersion());
        }

        return java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(java.util.Objects.requireNonNull(this.userRepository.save(existingUser))));
    }

    public @NonNull UserEntity getByEmail(@NonNull String email) {
        return java.util.Objects.requireNonNull(this.userRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException("Usuario no encontrado con email: " + email)));
    }

    public @NonNull UserResponse createUser(@NonNull UserPatchRequest request) {
        // Validar credenciales de usuario
        userValidationService.validateUserCredentials(request.getEmail(), request.getUsername());

        String verificationCode = verificationCodeService.generateVerificationCode();
        java.time.LocalDateTime expireAt = java.time.LocalDateTime.now().plusMinutes(15);

        // Crear y guardar usuario usando el método helper
        UserCreationData creationData = UserCreationData.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .countryId(request.getCountryId())
                .languageId(request.getLanguageId())
                .birthDate(request.getBirthDate())
                .enabled(false)
                .role(com.snapshot.chonect.utils.enums.Role.CUSTOMER)
                .customerType(request.getCustomerType())
                .termsVersion(request.getTermsVersion() != null ? request.getTermsVersion() : "1.0")
                .verificationCode(verificationCode)
                .verificationCodeExpireAt(expireAt)
                .build();
        UserEntity savedUser = java.util.Objects.requireNonNull(createUserFromData(creationData));

        // Enviar email de verificación
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getVerificationCode());

        return java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(savedUser));
    }

    /**
     * Método principal para crear usuario desde datos con validación de countryId y languageId
     * **AQUÍ ESTABA EL PROBLEMA ORIGINAL del countryId = 0**
     */
    @SuppressWarnings("null")
    public UserEntity createUserFromData(@NonNull UserCreationData data) {
        UserEntity newUser = UserEntity.builder()
                .username(data.getUsername())
                .email(data.getEmail())
                .password(passwordEncoder.encode(data.getPassword()))
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .enabled(data.isEnabled())
                .role(data.getRole())
                .customerType(data.getCustomerType())
                .termsVersion(data.getTermsVersion())
                .verificationCode(data.getVerificationCode())
                .verificationCodeExpireAt(data.getVerificationCodeExpireAt())
                .build();

        if (data.getCountryId() != null) {
            validateAndSetCountry(newUser, data.getCountryId());
        }

        if (data.getLanguageId() != null) {
            validateAndSetLanguage(newUser, data.getLanguageId());
        }

        if (data.getBirthDate() != null) {
            newUser.setBirthDate(data.getBirthDate());
        }
        
        return Objects.requireNonNull(userRepository.save(newUser));
    }

    /**
     * Valida que el countryId sea válido y existe en la base de datos
     * Previene el error del countryId = 0
     */
    private void validateAndSetCountry(UserEntity user, UUID countryId) {
        if (countryId == null) {
            return; // Si es null, no se asigna país
        }

        // Validar que el UUID no sea un UUID vacío o inválido
        if (countryId.toString().equals("00000000-0000-0000-0000-000000000000")) {
            throw new BadRequestException("ID de país inválido. No se puede usar UUID vacío.");
        }

        try {
            CountryEntity country = countryService.getById(countryId);
            user.setCountry(country);
        } catch (Exception e) {
            throw new BadRequestException("País no encontrado con el ID proporcionado: " + countryId);
        }
    }

    /**
     * Valida que el languageId sea válido y existe en la base de datos
     */
    private void validateAndSetLanguage(UserEntity user, UUID languageId) {
        if (languageId == null) {
            return; // Si es null, no se asigna idioma
        }
        
        // Validar que el UUID no sea un UUID vacío o inválido
        if (languageId.toString().equals("00000000-0000-0000-0000-000000000000")) {
            throw new BadRequestException("ID de idioma inválido. No se puede usar UUID vacío.");
        }
        
        try {
            LanguageEntity language = languageService.getById(languageId);
            user.setLanguage(language);
        } catch (Exception e) {
            throw new BadRequestException("Idioma no encontrado con el ID proporcionado: " + languageId);
        }
    }

    public static class UserCreationData {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private UUID countryId;  // **CAMBIADO DE Long A UUID**
        private UUID languageId; // **CAMBIADO DE Long A UUID**
        private LocalDate birthDate;
        private boolean enabled;
        private com.snapshot.chonect.utils.enums.Role role;
        private CustomerType customerType;
        private String termsVersion;
        private String verificationCode;
        private java.time.LocalDateTime verificationCodeExpireAt;

        private UserCreationData() {}

        private UserCreationData(Builder builder) {
            this.username = builder.username;
            this.email = builder.email;
            this.password = builder.password;
            this.firstName = builder.firstName;
            this.lastName = builder.lastName;
            this.countryId = builder.countryId;
            this.languageId = builder.languageId;
            this.birthDate = builder.birthDate;
            this.enabled = builder.enabled;
            this.role = builder.role;
            this.customerType = builder.customerType;
            this.termsVersion = builder.termsVersion;
            this.verificationCode = builder.verificationCode;
            this.verificationCodeExpireAt = builder.verificationCodeExpireAt;
        }

        @NonNull
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String username;
            private String email;
            private String password;
            private String firstName;
            private String lastName;
            private UUID countryId;  // **CAMBIADO DE Long A UUID**
            private UUID languageId; // **CAMBIADO DE Long A UUID**
            private LocalDate birthDate;
            private boolean enabled;
            private com.snapshot.chonect.utils.enums.Role role;
            private CustomerType customerType;
            private String termsVersion;
            private String verificationCode;
            private java.time.LocalDateTime verificationCodeExpireAt;

            public Builder username(String username) {
                this.username = username;
                return this;
            }

            public Builder email(String email) {
                this.email = email;
                return this;
            }

            public Builder password(String password) {
                this.password = password;
                return this;
            }

            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public Builder countryId(UUID countryId) {
                this.countryId = countryId;
                return this;
            }

            public Builder languageId(UUID languageId) {
                this.languageId = languageId;
                return this;
            }

            public Builder birthDate(LocalDate birthDate) {
                this.birthDate = birthDate;
                return this;
            }

            public Builder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public Builder role(com.snapshot.chonect.utils.enums.Role role) {
                this.role = role;
                return this;
            }

            public Builder customerType(CustomerType customerType) {
                this.customerType = customerType;
                return this;
            }

            public Builder termsVersion(String termsVersion) {
                this.termsVersion = termsVersion;
                return this;
            }

            public Builder verificationCode(String verificationCode) {
                this.verificationCode = verificationCode;
                return this;
            }

            public Builder verificationCodeExpireAt(java.time.LocalDateTime verificationCodeExpireAt) {
                this.verificationCodeExpireAt = verificationCodeExpireAt;
                return this;
            }

            @NonNull
            public UserCreationData build() {
                return new UserCreationData(this);
            }
        }

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public UUID getCountryId() { return countryId; }  // **CAMBIADO DE Long A UUID**
        public void setCountryId(UUID countryId) { this.countryId = countryId; }

        public UUID getLanguageId() { return languageId; } // **CAMBIADO DE Long A UUID**
        public void setLanguageId(UUID languageId) { this.languageId = languageId; }

        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public com.snapshot.chonect.utils.enums.Role getRole() { return role; }
        public void setRole(com.snapshot.chonect.utils.enums.Role role) { this.role = role; }

        public CustomerType getCustomerType() { return customerType; }
        public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

        public String getTermsVersion() { return termsVersion; }
        public void setTermsVersion(String termsVersion) { this.termsVersion = termsVersion; }

        public String getVerificationCode() { return verificationCode; }
        public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

        public java.time.LocalDateTime getVerificationCodeExpireAt() { return verificationCodeExpireAt; }
        public void setVerificationCodeExpireAt(java.time.LocalDateTime verificationCodeExpireAt) { this.verificationCodeExpireAt = verificationCodeExpireAt; }
    }
}
