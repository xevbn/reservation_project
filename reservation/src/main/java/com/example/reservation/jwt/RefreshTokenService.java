package com.example.reservation.jwt;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.example.reservation.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RedisSingleDataServiceImpl redisSingeDataServiceImpl;
    private final Duration duration = Duration.ofHours(1);

    //리프레시 토큰 생성
    public String generateRefreshToken(User user) {
        String token = jwtProvider.generateRefreshToken(user);

        String username = user.getUsername();
        redisSingeDataServiceImpl.setSingleData(token, username, duration);

        return token;
    }

    //리프레시 토큰 업데이트
    public String UpdateRefreshToken(User user, String refreshToken) {
        String newToken = jwtProvider.generateRefreshToken(user);

        redisSingeDataServiceImpl.deleteSingleData(refreshToken);
        redisSingeDataServiceImpl.setSingleData(newToken, user.getUsername(), duration);

        return newToken;
    }

    public void deleteByRefreshToken(String refresh) {
        redisSingeDataServiceImpl.deleteSingleData(refresh);
    }
}
