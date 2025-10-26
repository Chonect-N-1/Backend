package com.snapshot.chonect.utils;

import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.exceptions.BadRequestException;

@Service
public class UserValidationService {

    private final UserRepository userRepository;

    public UserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Valida que el email no esté registrado en el sistema.
     * @param email Email a validar.
     * @throws BadRequestException Si el email ya está registrado.
     */
    public void validateEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("El email ya está registrado");
        }
    }

    /**
     * Valida que el username no esté en uso.
     * @param username Username a validar.
     * @throws BadRequestException Si el username ya está en uso.
     */
    public void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
    }

    /**
     * Valida que tanto el email como el username no estén en uso.
     * @param email Email a validar.
     * @param username Username a validar.
     */
    public void validateUserCredentials(String email, String username) {
        validateEmailNotExists(email);
        validateUsernameNotExists(username);
    }
}
