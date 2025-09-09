package com.ejemplo.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ejemplo.demo.api.dto.auth.AuthenticationRequest;
import com.ejemplo.demo.api.dto.auth.AuthenticationResponse;
import com.ejemplo.demo.domain.entities.UserEntity;
import com.ejemplo.demo.domain.entities.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(UserEntity user) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Codificar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Guardar el usuario
        UserEntity savedUser = userRepository.save(user);
        
        // Convert UserEntity to UserDetails
        UserDetails userDetails = User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRole().name())
            .build();
            
        // Generate JWT token
        String jwtToken = jwtService.generateToken(userDetails);
        
        return new AuthenticationResponse(
            jwtToken,
            savedUser.getUsername(),
            savedUser.getRole().name()
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Autenticar al usuario
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
            )
        );
        
        // Obtener el usuario
        UserEntity user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        // Convert UserEntity to UserDetails
        UserDetails userDetails = User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRole().name())
            .build();
        
        // Generate JWT token
        String jwtToken = jwtService.generateToken(userDetails);
        
        return new AuthenticationResponse(
            jwtToken,
            user.getUsername(),
            user.getRole().name()
        );
    }
}
