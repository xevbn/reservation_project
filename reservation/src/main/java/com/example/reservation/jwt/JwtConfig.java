package com.example.reservation.jwt;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {
    private final static Long expireTime = 60 * 5 * 1000L;      //액세스 토큰 5분
    private final static Long refreshExpire = 60 * 60 * 1000L;      //리프레시 토큰 1시간
    
    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public Long getExpiry() {
        return expireTime;
    }

    public Long getRefreshExpiry() {
        return refreshExpire;
    }
}
