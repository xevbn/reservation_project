package com.example.reservation.jwt;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    public Optional<RefreshToken> findByUsername(String username);
    public Optional<RefreshToken> findByUserId(Long userId);
    public void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
