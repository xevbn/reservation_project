package com.example.reservation.Security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.example.reservation.user.User;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey key;
    private final JwtConfig jwtConfig;

    //jwt 발급
    public String createToken(User user) {
        String id = user.getId().toString();
        //사용자 권한 설정 추가 요망
        String authorities = "";
        
        //jwt에 넣을 정보(필요 시 추가 요망)
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", id);
        claims.put("scope", authorities);

        //jwt 서명해서 반환
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiry()))
            .signWith(key)
            .compact();
    }

    //refreshToken 생성
    public String generateRefreshToken(User user) {
        String id = user.getId().toString();

        //jwt 서명해서 반환
        return Jwts.builder()
            .setSubject(id)
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
