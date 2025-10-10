package com.snapshot.chonect.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // francamente yo me considero medianamente inteligente 
    // peeeeeero esta cosa me hace creer lo contrario
    // sinceramente no se que hace creo que esta modificando los filtros de seguridad 
    // que utiliza la api rest 

    // conclucion: lo hizo un mago, deep seek va tener mucho trabajo pa ensenarme jjajajaja
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // el csrf es una especie de protecion para evitar engaños al navegador
            // para que realice acciones no deseadas en una aplicación en la que el usuario está autenticado.
            .csrf(csrf ->csrf.disable())
            // si mal no recuerdo authorizeHttpRequests sirve como middleware 
            // puede llegar a dar errores si no se maneja bien
            // ya que proive que las rutas sean publicas a excepcion de "/auth/login", "/auth/register"
            // si es necesario se tiene que agregar mas a un futuro cercano

            .authorizeHttpRequests(auth -> auth
                // pero carlos si lo sacas a producion cambialo
                    .requestMatchers(
                        "/api/v1/auth/signup",
                        "/api/v1/auth/login",
                        "/api/v1/auth/verify",
                        "/api/v1/auth/resend",
                        "/api/v1/user/**",
                        "/graphql/**",
                        "/graphiql",
                        "/graphiql/**"
                    ).permitAll().anyRequest().permitAll()

                    .requestMatchers(
                        "/api/v1/auth/delete"
                    ).authenticated().anyRequest().authenticated()
            //     // .requestMatchers("/admin/**").hasRole("ADMIN")
            //     // .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                    
            )
            
            // este punto vuelve stateless la aplicacion (que es volver todas las apis desconectada)
            // la IA me mando estos comportamientos:
            // ~ No crea sesiones HTTP en el servidor
            // ~ No usa cookies de sesión (JSESSIONID)
            // ~ Cada request es independiente - sin estado mantenido
            // ~ El cliente debe enviar credenciales en cada solicitud

            // y me viene muy bien con la implementacion de Graphql, en cualquier caso
            // cambio la app a stateful que esta por defecto
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // estas dos ultimas son solo para implementar el filtro jwt
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource (){
        CorsConfiguration configuration = new CorsConfiguration();
        // no me gusta mucho dejar los datos asi de expuestos pero creo que son temporales 
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080/**", "https://chonect-n-1.onrender.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));  // ← AGREGAR ESTO
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
