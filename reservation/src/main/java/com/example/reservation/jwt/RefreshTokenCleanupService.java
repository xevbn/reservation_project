package com.example.reservation.jwt;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService {
    private final RefreshTokenRepository repo;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanUpExpiredTokens() {
        repo.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
