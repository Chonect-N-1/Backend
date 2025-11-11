package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.api.dto.request.LoginUserDto;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import com.snapshot.chonect.utils.messages.ErrorMessages;
import com.snapshot.chonect.utils.VerificationCodeService;

@Service
public class AuthenticationService {

    private static final String EMAIL_NULL_MESSAGE = "Email cannot be null";

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    // parece que lombok da error con el auth
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            VerificationCodeService verificationCodeService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }



    public UserEntity authenticate(LoginUserDto input) {
        String email = Objects.requireNonNull(input.getEmail(), EMAIL_NULL_MESSAGE);
        UserEntity user = this.userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.nameNotFound("Usuario")));
        if (!user.isEnabled()) {
            throw new UnauthorizedException(ErrorMessages.cuentaNotVerificate("Usuario"));
        } else {
            this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
            return user;
        }
    }

    public void resendVerificationCode(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            if (user.isEnabled()) {
                throw new BadRequestException("Account is already verified");
            }

            user.setVerificationCode(verificationCodeService.generateVerificationCode());
            user.setVerificationCodeExpireAt(LocalDateTime.now().plusHours(1));

            // ✅ Verificación segura con Objects.requireNonNull
            String safeEmail = Objects.requireNonNull(user.getEmail(), EMAIL_NULL_MESSAGE);
            String safeFirstName = Objects.requireNonNull(user.getFirstName(), "First name cannot be null");
            String safeVerificationCode = Objects.requireNonNull(user.getVerificationCode(), "Verification code cannot be null");

            emailService.sendVerificationEmail(safeEmail, safeFirstName, safeVerificationCode);

            userRepository.save(user);
        } else {
            throw new IdNotFoundException("User not found");
        }
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        // Validar que el email no sea null
        Objects.requireNonNull(email, EMAIL_NULL_MESSAGE);

        // Buscar el usuario por email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.emailNotFound("Usuario con email: " + email)));

        // Eliminar directamente la entidad (más eficiente que deleteById)
        userRepository.delete(user);
    }


}
