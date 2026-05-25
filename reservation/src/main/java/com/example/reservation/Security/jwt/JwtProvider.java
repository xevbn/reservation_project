package com.example.reservation.Security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.example.reservation.domain.user.UserDomain;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey key;
    private final JwtConfig jwtConfig;

    //jwt 발급
    public String createToken(Long userId) {
        //jwt 서명해서 반환
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiry()))
            .signWith(key)
            .compact();
    }

    //refreshToken 생성
    public String generateRefreshToken(Long userId) {
        //jwt 서명해서 반환
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiry()))
            .signWith(key)
            .compact();
    }

    //jwt가 유효한지 판단
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //jwt에서 username 반환
    public String getUsername(String token) {
        String sub = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();

        String[] str = sub.split("_");

        return str[0];
    }

    public Long getUserId(String token) {
        Long userId = Long.valueOf(Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject());

        return userId;
    }
}
