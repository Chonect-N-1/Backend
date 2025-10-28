package com.snapshot.chonect.infrastructure.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final JwtProperties jwtProperties;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.info("Generating JWT token for user: {}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails, jwtProperties.getExpirationTime());
    }

    public long getExpirationTime() {
        return jwtProperties.getExpirationTime();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        String secretKey = jwtProperties.getSecretKey();
        logger.info("JWT Secret Key loaded: {}", secretKey != null ? "present (length: " + secretKey.length() + ")" : "null");
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.error("JWT secret key is not configured");
            throw new IllegalArgumentException("JWT secret key is not configured. Please set the SECURITY_JWT_SECRET_KEY environment variable.");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            logger.info("JWT Secret Key decoded successfully, key length: {}", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Failed to decode JWT secret key: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT secret key format. Ensure it is a valid Base64 encoded string.", e);
        }
    }
}
