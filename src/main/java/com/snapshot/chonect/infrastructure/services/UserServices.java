package com.snapshot.chonect.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@AllArgsConstructor
public class UserServices implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServices.class);

    private static final String USERNAME_NULL_MESSAGE = "username cannot be null";
    private static final String EMAIL_NULL_MESSAGE = "email cannot be null";
    private static final String PASSWORD_NULL_MESSAGE = "password cannot be null";
    private static final String FIRST_NAME_NULL_MESSAGE = "firstName cannot be null";
    private static final String LAST_NAME_NULL_MESSAGE = "lastName cannot be null";
    private static final String TERMS_VERSION_NULL_MESSAGE = "termsVersion cannot be null";

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
        logger.info("Buscando usuario con ID: {}", id);
        UserEntity userEntity = java.util.Objects.requireNonNull(this.supportService.findById(userRepository, id, "UserEntity"));
        UserResponse response = java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(userEntity));
        logger.debug("Usuario encontrado exitosamente: {}", userEntity.getUsername());
        return response;
    }

    @Override
    @Transactional
    public @NonNull UserResponse update(UserUpdateRequest userRequest, UUID id) {
        UserEntity userUpdate = java.util.Objects.requireNonNull(this.userMapper.requestUpdateToEntity(userRequest));
        userUpdate.setId(id);
        // Encriptar la contraseña si está presente
        if (userRequest.getPassword() != null && !userRequest.getPassword().trim().isEmpty()) {
            userUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        return java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(java.util.Objects.requireNonNull(this.userRepository.save(userUpdate))));
    }

    public @NonNull UserResponse patch(@NonNull UserPatchRequest request, @NonNull UUID id) {
        return patch(request, id, null);
    }

    @Transactional
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
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
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

    @Transactional
    public @NonNull UserResponse createUser(@NonNull UserPatchRequest request) {
        logger.info("Iniciando creación de usuario con email: {}", request.getEmail());

        // Validar credenciales de usuario
        userValidationService.validateUserCredentials(request.getEmail(), request.getUsername());
        logger.debug("Validación de credenciales exitosa para usuario: {}", request.getUsername());

        String verificationCode = verificationCodeService.generateVerificationCode();
        java.time.LocalDateTime expireAt = java.time.LocalDateTime.now().plusMinutes(15);
        logger.debug("Código de verificación generado para usuario: {}", request.getUsername());

        // Crear y guardar usuario usando el método helper
        String username = Objects.requireNonNull(request.getUsername(), USERNAME_NULL_MESSAGE);
        String email = Objects.requireNonNull(request.getEmail(), EMAIL_NULL_MESSAGE);
        String password = Objects.requireNonNull(request.getPassword(), PASSWORD_NULL_MESSAGE);
        String firstName = Objects.requireNonNull(request.getFirstName(), FIRST_NAME_NULL_MESSAGE);
        String lastName = Objects.requireNonNull(request.getLastName(), LAST_NAME_NULL_MESSAGE);

        UserCreationData creationData = UserCreationData.builder()
                .username(username)
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
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
        logger.info("Usuario creado exitosamente con ID: {}", savedUser.getId());

        // Enviar email de verificación
        emailService.sendVerificationEmail(
            Objects.requireNonNull(savedUser.getEmail()),
            Objects.requireNonNull(savedUser.getFirstName()),
            Objects.requireNonNull(savedUser.getVerificationCode())
        );
        logger.debug("Email de verificación enviado a: {}", savedUser.getEmail());

        UserResponse response = java.util.Objects.requireNonNull(this.userMapper.userEntityToUserResponse(savedUser));
        logger.info("Creación de usuario completada exitosamente para: {}", savedUser.getUsername());
        return response;
    }

    /**
     * Método principal para crear usuario desde datos con validación de countryId y languageId
     * **AQUÍ ESTABA EL PROBLEMA ORIGINAL del countryId = 0**
     */
    @SuppressWarnings("null")
    @Transactional
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

        // Validar formato UUID adicional
        try {
            UUID.fromString(countryId.toString()); // Verifica que sea un UUID válido
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Formato de UUID de país inválido: " + countryId);
        }

        try {
            CountryEntity country = countryService.getById(countryId);
            user.setCountry(country);
        } catch (IdNotFoundException e) {
            throw new BadRequestException("País no encontrado con el ID proporcionado: " + countryId);
        } catch (Exception e) {
            throw new BadRequestException("Error al validar país con ID: " + countryId);
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

        // Validar formato UUID adicional
        try {
            UUID.fromString(languageId.toString()); // Verifica que sea un UUID válido
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Formato de UUID de idioma inválido: " + languageId);
        }

        try {
            LanguageEntity language = languageService.getById(languageId);
            user.setLanguage(language);
        } catch (IdNotFoundException e) {
            throw new BadRequestException("Idioma no encontrado con el ID proporcionado: " + languageId);
        } catch (Exception e) {
            throw new BadRequestException("Error al validar idioma con ID: " + languageId);
        }
    }

    public static class UserCreationData {
        private @NonNull String username;
        private @NonNull String email;
        private @NonNull String password;
        private @NonNull String firstName;
        private @NonNull String lastName;
        private UUID countryId;  // **CAMBIADO DE Long A UUID**
        private UUID languageId; // **CAMBIADO DE Long A UUID**
        private LocalDate birthDate;
        private boolean enabled;
        private com.snapshot.chonect.utils.enums.Role role;
        private CustomerType customerType;
        private @NonNull String termsVersion;
        private String verificationCode;
        private java.time.LocalDateTime verificationCodeExpireAt;

        private UserCreationData(Builder builder) {
            this.username = Objects.requireNonNull(builder.username, USERNAME_NULL_MESSAGE);
            this.email = Objects.requireNonNull(builder.email, EMAIL_NULL_MESSAGE);
            this.password = Objects.requireNonNull(builder.password, PASSWORD_NULL_MESSAGE);
            this.firstName = Objects.requireNonNull(builder.firstName, FIRST_NAME_NULL_MESSAGE);
            this.lastName = Objects.requireNonNull(builder.lastName, LAST_NAME_NULL_MESSAGE);
            this.countryId = builder.countryId;
            this.languageId = builder.languageId;
            this.birthDate = builder.birthDate;
            this.enabled = builder.enabled;
            this.role = builder.role;
            this.customerType = builder.customerType;
            this.termsVersion = Objects.requireNonNull(builder.termsVersion, TERMS_VERSION_NULL_MESSAGE);
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
        public @NonNull String getUsername() { return username; }
        public void setUsername(@NonNull String username) { this.username = Objects.requireNonNull(username, USERNAME_NULL_MESSAGE); }

        public @NonNull String getEmail() { return email; }
        public void setEmail(@NonNull String email) { this.email = Objects.requireNonNull(email, EMAIL_NULL_MESSAGE); }

        public @NonNull String getPassword() { return password; }
        public void setPassword(@NonNull String password) { this.password = Objects.requireNonNull(password, PASSWORD_NULL_MESSAGE); }

        public @NonNull String getFirstName() { return firstName; }
        public void setFirstName(@NonNull String firstName) { this.firstName = Objects.requireNonNull(firstName, FIRST_NAME_NULL_MESSAGE); }

        public @NonNull String getLastName() { return lastName; }
        public void setLastName(@NonNull String lastName) { this.lastName = Objects.requireNonNull(lastName, LAST_NAME_NULL_MESSAGE); }

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

        public @NonNull String getTermsVersion() { return termsVersion; }
        public void setTermsVersion(@NonNull String termsVersion) { this.termsVersion = Objects.requireNonNull(termsVersion, TERMS_VERSION_NULL_MESSAGE); }

        public String getVerificationCode() { return verificationCode; }
        public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

        public java.time.LocalDateTime getVerificationCodeExpireAt() { return verificationCodeExpireAt; }
        public void setVerificationCodeExpireAt(java.time.LocalDateTime verificationCodeExpireAt) { this.verificationCodeExpireAt = verificationCodeExpireAt; }
    }
}
