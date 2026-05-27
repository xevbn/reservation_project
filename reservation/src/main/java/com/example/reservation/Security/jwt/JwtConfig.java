package com.example.reservation.Security.jwt;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {
    private final static Long expireTime = 60 * 60 * 1000L;
    private final static Long refreshExpire = 60 * 60 * 24 * 7 * 1000L;
    
    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public static Long getExpiry() {
        return expireTime;
    }

    public static Long getRefreshExpiry() {
        return refreshExpire;
    }
}
