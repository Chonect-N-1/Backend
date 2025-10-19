package com.snapshot.chonect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.snapshot.chonect.domain.repositories.UserRepository;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.messages.ErrorMessages;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ApplicationConfiguration {

    private final UserRepository userRepository;

    @Bean
    UserDetailsService userDetailsService() {
        return (username) -> {
            return (UserDetails) this.userRepository.findByEmail(username).orElseThrow(() -> {
                return new IdNotFoundException(ErrorMessages.nameNotFound("Usuario"));
            });
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // public UserDetails loadUserByUsername(String usernameOrEmail) throws
    // UsernameNotFoundException {
    // // Buscamos el usuario en la base de datos usando el mismo valor para ambos
    // parámetros
    // UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail,
    // usernameOrEmail)
    // .orElseThrow(() -> new UsernameNotFoundException("No se encontró un usuario
    // con el nombre o email: " + usernameOrEmail));

    // // Creas y devuelves un objeto UserDetails a partir del usuario encontrado.
    // return new User(
    // user.getUsername(),
    // user.getPassword(),
    // // Aquí irían los roles/autoridades del usuario, por ahora lo dejamos vacío.
    // java.util.Collections.emptyList()
    // );
    // }
}
