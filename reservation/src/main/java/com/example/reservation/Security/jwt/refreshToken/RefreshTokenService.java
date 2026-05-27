package com.example.reservation.Security.jwt.refreshToken;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.reservation.Security.jwt.JwtConfig;
import com.example.reservation.Security.jwt.JwtProvider;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.user.UserDomain;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    //리프레시 토큰 생성
    public String generateRefreshToken(UserDomain user) {
        String token = jwtProvider.generateRefreshToken(user.getId());
        refreshTokenRepository.save(user.getId(), token, Instant.now().plusSeconds(60 * 60 * 24 * 7));

        return token;
    }

    //userId를 통해 리프레시 토큰 검색
    public String getRefreshTokenByUserId(Long id) {
        return refreshTokenRepository.findByUserId(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    //리프레시 토큰 업데이트
    public String UpdateRefreshToken(Long userId) {
        refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        String newToken = jwtProvider.generateRefreshToken(userId);

        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.save(
            userId, 
            newToken, 
            Instant.now().plusMillis(JwtConfig.getRefreshExpiry()));

        return newToken;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
