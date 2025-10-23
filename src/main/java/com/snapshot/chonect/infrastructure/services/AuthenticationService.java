package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.api.dto.request.LoginUserDto;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import com.snapshot.chonect.utils.messages.ErrorMessages;
import com.snapshot.chonect.utils.VerificationCodeService;
import com.snapshot.chonect.utils.EmailTemplateService;
import com.snapshot.chonect.utils.UserValidationService;

// import lombok.AllArgsConstructor;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final EmailTemplateService emailTemplateService;
    private final UserValidationService userValidationService;

    // parece que lombok da error con el auth
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationCodeService verificationCodeService,
            EmailTemplateService emailTemplateService,
            UserValidationService userValidationService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.emailTemplateService = emailTemplateService;
        this.userValidationService = userValidationService;
    }



    public UserEntity authenticate(LoginUserDto input) {
        UserEntity user = (UserEntity) this.userRepository.findByUsernameOrEmail(input.getSearch(), input.getSearch())
                .orElseThrow(() -> new BadRequestException(ErrorMessages.nameNotFound("Usuario")));
        if (!user.isEnabled()) {
            throw new UnauthorizedException(ErrorMessages.cuentaNotVerificate("Usuario"));
        } else {
            this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(input.getSearch(), input.getPassword()));
            return user;
        }
    }



    public void resendVerificationCode(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(verificationCodeService.generateVerificationCode());
            user.setVerificationCodeExpireAt(LocalDateTime.now().plusHours(1));
            try {
                emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), user.getVerificationCode());
            } catch (BadRequestException e) {
                throw e;
            }
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    public void deleteUserByEmail(String email) {
    // Busca al usuario por su email. Si no lo encuentra, lanza una excepción.
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.emailNotFound("Usuario con email: " + email)));

        // Si el usuario existe, lo elimina.
        userRepository.deleteById(user.getId());
    }


}
