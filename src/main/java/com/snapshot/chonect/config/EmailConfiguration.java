package com.snapshot.chonect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmailConfiguration {

    @Value("${resend.api.key}")
    private String apiKey;

    public EmailConfiguration() {
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl("https://api.resend.com")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
