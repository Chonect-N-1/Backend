package com.snapshot.chonect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.3")
                .info(new Info()
                        .title("Chonect API")
                        .description("API REST y GraphQL para la creación de páginas web a través de un backend modular")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Chonect")
                                .email("contacto@chonect.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de desarrollo local"))
                .addServersItem(new Server()
                        .url("https://chonect-n-1.onrender.com")
                        .description("Servidor de producción"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                            new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
