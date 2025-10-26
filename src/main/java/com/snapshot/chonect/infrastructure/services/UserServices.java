package com.snapshot.chonect.infrastructure.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServices implements IUserService {

    private final UserMappers userMapper;

    private final UserRepository userRepository;

    private final SupportService<UserEntity> supportService;

    private final CountryService countryService;

    private final LanguageService languageService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final VerificationCodeService verificationCodeService;

    private final UserValidationService userValidationService;

    @Override
    public UserResponse getById(Long id) {
        UserEntity userEntity = this.supportService.findById(userRepository, id, "UserEntity");
        return this.userMapper.userEntityToUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long id) {
        UserEntity userUpdate = this.userMapper.requestUpdateToEntity(request);
        userUpdate.setId(id);
        return this.userMapper.userEntityToUserResponse(this.userRepository.save(userUpdate));
    }

    public UserResponse patch(UserPatchRequest request, Long id) {
        return patch(request, id, null);
    }

    public UserResponse patch(UserPatchRequest request, Long id, UserEntity authenticatedUser) {
        UserEntity existingUser = this.supportService.findById(userRepository, id, "UserEntity");

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
            CountryEntity country = countryService.getById(request.getCountryId());
            existingUser.setCountry(country);
        }
        if (request.hasLanguageId()) {
            LanguageEntity language = languageService.getById(request.getLanguageId());
            existingUser.setLanguage(language);
        }
        if (request.hasBirthDate()) {
            existingUser.setBirthDate(request.getBirthDate().toString());
        }
        if (request.hasCustomerType()) {
            existingUser.setCustomerType(request.getCustomerType());
        }
        if (request.hasTermsVersion()) {
            existingUser.setTermsVersion(request.getTermsVersion());
        }

        return this.userMapper.userEntityToUserResponse(this.userRepository.save(existingUser));
    }

    public UserEntity getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException("Usuario no encontrado con email: " + email));
    }

    public UserResponse createUser(UserPatchRequest request) {
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
                .birthDate(request.getBirthDate() != null ? request.getBirthDate().toString() : null)
                .enabled(false)
                .role(com.snapshot.chonect.utils.enums.Role.CUSTOMER)
                .customerType(request.getCustomerType())
                .termsVersion(request.getTermsVersion() != null ? request.getTermsVersion() : "1.0")
                .verificationCode(verificationCode)
                .verificationCodeExpireAt(expireAt)
                .build();
        UserEntity savedUser = createUserFromData(creationData);

        // Enviar email de verificación
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getVerificationCode());

        return this.userMapper.userEntityToUserResponse(savedUser);
    }

    public UserEntity createUserFromData(UserCreationData data) {
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
            CountryEntity country = countryService.getById(data.getCountryId());
            newUser.setCountry(country);
        }

        if (data.getLanguageId() != null) {
            LanguageEntity language = languageService.getById(data.getLanguageId());
            newUser.setLanguage(language);
        }

        if (data.getBirthDate() != null) {
            newUser.setBirthDate(data.getBirthDate());
        }

        return userRepository.save(newUser);
    }

    public static class UserCreationData {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Long countryId;
        private Long languageId;
        private String birthDate;
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

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String username;
            private String email;
            private String password;
            private String firstName;
            private String lastName;
            private Long countryId;
            private Long languageId;
            private String birthDate;
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

            public Builder countryId(Long countryId) {
                this.countryId = countryId;
                return this;
            }

            public Builder languageId(Long languageId) {
                this.languageId = languageId;
                return this;
            }

            public Builder birthDate(String birthDate) {
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

        public Long getCountryId() { return countryId; }
        public void setCountryId(Long countryId) { this.countryId = countryId; }

        public Long getLanguageId() { return languageId; }
        public void setLanguageId(Long languageId) { this.languageId = languageId; }

        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

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
