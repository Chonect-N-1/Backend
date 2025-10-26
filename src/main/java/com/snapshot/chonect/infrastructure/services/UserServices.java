package com.snapshot.chonect.infrastructure.services;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServices implements IUserService {

    @Autowired
    private final UserMappers userMapper;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final SupportService<UserEntity> supportService;

    @Autowired
    private final CountryService countryService;

    @Autowired
    private final LanguageService languageService;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final VerificationCodeService verificationCodeService;

    @Autowired
    private final UserValidationService userValidationService;

    @Override
    public UserResponse getById(Long id) {
        UserEntity userEntity = this.supportService.findById(userRepository, id, "UserEntity");
        return this.userMapper.userEntityToUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long id) {
        // dejo este codigo porque lo mas probable es que lo necesite en un futuro cercano
        // UserEntity userEntity = this.supportService.find(userRepository, id, "UserEntity");
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
            throw new RuntimeException("No tienes permisos para modificar esta cuenta");
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    public UserResponse createUser(UserPatchRequest request) {
        // Validar credenciales de usuario
        userValidationService.validateUserCredentials(request.getEmail(), request.getUsername());

        String verificationCode = verificationCodeService.generateVerificationCode();
        java.time.LocalDateTime expireAt = java.time.LocalDateTime.now().plusMinutes(15);

        // Crear y guardar usuario usando el método helper
        UserEntity savedUser = createUserFromData(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getCountryId(),
                request.getLanguageId(),
                request.getBirthDate() != null ? request.getBirthDate().toString() : null,
                false,
                com.snapshot.chonect.utils.enums.Role.CUSTOMER,
                request.getCustomerType(),
                request.getTermsVersion() != null ? request.getTermsVersion() : "1.0",
                verificationCode,
                expireAt
        );

        // Enviar email de verificación
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getVerificationCode());
        } catch (BadRequestException e) {
            throw e;
        }

        return this.userMapper.userEntityToUserResponse(savedUser);
    }

    public UserEntity createUserFromData(String username, String email, String password, String firstName, String lastName, Long countryId, Long languageId, String birthDate, boolean enabled, com.snapshot.chonect.utils.enums.Role role, CustomerType customerType, String termsVersion, String verificationCode, java.time.LocalDateTime verificationCodeExpireAt) {
        UserEntity newUser = UserEntity.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(enabled)
                .role(role)
                .customerType(customerType)
                .termsVersion(termsVersion)
                .verificationCode(verificationCode)
                .verificationCodeExpireAt(verificationCodeExpireAt)
                .build();

        if (countryId != null) {
            CountryEntity country = countryService.getById(countryId);
            newUser.setCountry(country);
        }

        if (languageId != null) {
            LanguageEntity language = languageService.getById(languageId);
            newUser.setLanguage(language);
        }

        if (birthDate != null) {
            newUser.setBirthDate(birthDate);
        }

        return userRepository.save(newUser);
    }
}
