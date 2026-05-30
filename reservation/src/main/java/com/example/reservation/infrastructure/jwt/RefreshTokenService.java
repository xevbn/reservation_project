package com.example.reservation.infrastructure.jwt;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.example.reservation.infrastructure.redis.RedisSingleDataServiceImpl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RedisSingleDataServiceImpl redisSingeDataServiceImpl;
    private final Duration duration = Duration.ofHours(1);

    //리프레시 토큰 생성
    public String generateRefreshToken(Long userId) {
        String token = jwtProvider.generateRefreshToken(userId);

        redisSingeDataServiceImpl.setSingleData(token, userId.toString(), duration);

        return token;
    }

    //리프레시 토큰 업데이트
    public String UpdateRefreshToken(Long userId, String refreshToken) {
        String newToken = jwtProvider.generateRefreshToken(userId);

        redisSingeDataServiceImpl.deleteSingleData(refreshToken);
        redisSingeDataServiceImpl.setSingleData(newToken, userId.toString(), duration);

        return newToken;
    }

    public void deleteByRefreshToken(String refresh) {
        redisSingeDataServiceImpl.deleteSingleData(refresh);
    }
}
