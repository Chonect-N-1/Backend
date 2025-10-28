package com.snapshot.chonect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    private String secretKey;
    private Long expirationTime;

    @PostConstruct
    public void init() {
        logger.info("JwtProperties loaded - Secret Key: {}", secretKey != null ? "present (length: " + secretKey.length() + ")" : "null");
        logger.info("JwtProperties loaded - Expiration Time: {}", expirationTime);
    }
}
