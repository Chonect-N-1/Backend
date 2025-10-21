package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.api.dto.request.VerifyUserDto;
import com.snapshot.chonect.api.dto.request.LoginUserDto;
import com.snapshot.chonect.api.dto.request.RegisterRequest;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.enums.Role;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

import jakarta.mail.MessagingException;

// import lombok.AllArgsConstructor;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // parece que lombok da error con el auth
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UserEntity signup(RegisterRequest registerUserDto) {

        if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            throw new BadRequestException("El email ya está registrado");
        }
    
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }

        UserEntity user = UserEntity.builder()
        .username(registerUserDto.getUsername())
        .email(registerUserDto.getEmail())
        .password(passwordEncoder.encode(registerUserDto.getPassword()))
        .firstName(registerUserDto.getFirstName())
        .lastName(registerUserDto.getLastName())
        .enabled(false)
        .role(Role.CUSTOMER)
        .verificationCode(generateVerificationCode())
        .verificationCodeExpireAt(LocalDateTime.now().plusMinutes(15))
        .build();

        sendVerificationEmail(user);
        return userRepository.save(user);
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

    public void verifyUser(VerifyUserDto input) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(input.getEmail());
        // tecnicamente podia hacer el codigo mas bonito simplemente negando los if pero
        // funciona jajajaj

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (user.getVerificationCodeExpireAt().isBefore(LocalDateTime.now())) {
                throw new BadRequestException(ErrorMessages.verificationTimeExpired("Usuario"));
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpireAt(null);
                userRepository.save(user);
            } else {
                throw new BadRequestException(ErrorMessages.verificationCodeInvalid("Usuario"));
            }
        } else {
            throw new IdNotFoundException(ErrorMessages.nameNotFound("Usuario"));
        }
    }

    public void resendVerificationCode(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpireAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(UserEntity user) {
        String subject = "Verificacion de cuenta - Chonect";
        String verificationCode = user.getVerificationCode();

        // Versión HTML del mensaje
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">¡Código de verificación de Chonect!</h2>"
                + "<p style=\"font-size: 16px;\">Por favor ingresa el código de verificación para continuar:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Código de verificación:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Versión de texto plano del mensaje
        String textMessage = "¡Hola!\n\n"
                + "Gracias por registrarte en Chonect.\n\n"
                + "Tu código de verificación es: " + verificationCode + "\n\n"
                + "Por favor ingresa este código en la aplicación para verificar tu cuenta.\n\n"
                + "Si no solicitaste este registro, puedes ignorar este mensaje.\n\n"
                + "Saludos,\n"
                + "El equipo de Chonect";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, textMessage, htmlMessage);
        } catch (MessagingException e) {
            logger.error("Error de mensajería al enviar correo de verificación a {}: {}", user.getEmail(), e.getMessage(), e);

            // Analizar el tipo de error para dar mejor feedback al usuario
            String errorMessage = analyzeEmailError(e);

            if (errorMessage.contains("SMTP") || errorMessage.contains("conexión")) {
                throw new BadRequestException("Error de conexión con el servidor de correo. El correo podría enviarse en unos minutos. Si el problema persiste, contacte al soporte.");
            } else if (errorMessage.contains("crítico") || errorMessage.contains("intentos")) {
                throw new BadRequestException("Error crítico al enviar correo de verificación. Por favor, contacte al administrador del sistema.");
            } else {
                throw new BadRequestException("Error al enviar correo de verificación. Por favor, inténtelo de nuevo más tarde.");
            }
        } catch (Exception e) {
            logger.error("Error inesperado al enviar correo de verificación a {}: {}", user.getEmail(), e.getMessage(), e);
            throw new BadRequestException("Error interno del servidor. Por favor, contacte al administrador.");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    // Método para analizar errores de correo y proporcionar información útil
    private String analyzeEmailError(MessagingException e) {
        String message = e.getMessage().toLowerCase();
        String cause = e.getCause() != null ? e.getCause().getMessage().toLowerCase() : "";

        if (message.contains("smtp") || cause.contains("smtp")) {
            return "Error SMTP";
        } else if (message.contains("conexión") || message.contains("connection") || cause.contains("connection")) {
            return "Error de conexión";
        } else if (message.contains("timeout") || cause.contains("timeout")) {
            return "Error de tiempo de espera";
        } else if (message.contains("autenticación") || message.contains("authentication") || cause.contains("authentication")) {
            return "Error de autenticación";
        } else if (message.contains("crítico") || message.contains("intentos") || message.contains("todos los intentos")) {
            return "Error crítico de correo";
        } else {
            return "Error general de correo";
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
