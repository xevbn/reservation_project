package com.example.reservation.jwt;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.reservation.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    //리프레시 토큰 생성
    public String generateRefreshToken(User user) {
        String token = jwtProvider.generateRefreshToken(user);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        RefreshToken saving = new RefreshToken(user.getId(), user.getUsername(), token, expiresAt);
        refreshTokenRepository.save(saving);

        return token;
    }

    //만료된 토큰 삭제
    public void deleteExpired() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    //userId를 통해 리프레시 토큰 검색
    public RefreshToken getRefreshTokenByUserId(Long id) {
        return refreshTokenRepository.findByUserId(id)
            .orElseThrow();
    }

    //리프레시 토큰 업데이트
    public String UpdateRefreshToken(User user) {
        RefreshToken saved = refreshTokenRepository.findByUserId(user.getId())
            .orElseThrow(() -> new EntityNotFoundException());

        String newToken = jwtProvider.generateRefreshToken(user);

        saved.setRefreshToken(newToken);
        saved.setExpiresAt(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(saved).getRefreshToken();
    }
}
