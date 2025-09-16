package com.chonect.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API Chonect N-1", version = "1.0", description = "Documentacion de la API de Chonect N-1"))
public class OpenApiConfig {

}