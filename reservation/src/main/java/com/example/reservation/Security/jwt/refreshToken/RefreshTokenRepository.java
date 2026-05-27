package com.example.reservation.Security.jwt.refreshToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
  private final StringRedisTemplate redisTemplate;

  public void save(@NonNull Long userId, @NonNull String refreshToken, @NonNull Instant expiresAt) {
    redisTemplate.opsForValue()
      .set(userId.toString(), refreshToken, Duration.between(Instant.now(), expiresAt));
  }

  public Optional<String> findByUserId(@NonNull Long userId) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(userId.toString()));
  }

  public void deleteByUserId(@NonNull Long userId) {
    redisTemplate.delete(userId.toString());
  }
}