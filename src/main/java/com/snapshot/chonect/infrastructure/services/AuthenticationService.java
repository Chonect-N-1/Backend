package com.snapshot.chonect.infrastructure.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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

// import lombok.AllArgsConstructor;

@Service
public class AuthenticationService {

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
        .fullName(registerUserDto.getFullName() != null ? registerUserDto.getFullName() : registerUserDto.getUsername()) 
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
        String subject = "verificacion de cuenta";
        String verificationCode = user.getVerificationCode();

        // esta es una solucion temporal, cuando ya tenga todo pulido la cambio
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Codigo de verificacion de nuestra app!</h2>"
                + "<p style=\"font-size: 16px;\">porfavor ingresa el codigo de verificacion antes de continuar:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Codigo de verificacion:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            System.out.println(e);
            throw new BadRequestException(e.getMessage());
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    
    public void deleteUserByEmail(String email) {
    // Busca al usuario por su email. Si no lo encuentra, lanza una excepción.
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException(ErrorMessages.emailNotFound("Usuario con email: " + email)));

        // Si el usuario existe, lo elimina.
        userRepository.deleteById(user.getId());
    }


}
