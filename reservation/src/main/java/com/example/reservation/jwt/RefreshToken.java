package com.example.reservation.jwt;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String username;
    private String refreshToken;
    private LocalDateTime expiresAt;

    public RefreshToken(Long userId, String username, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.username = username;
        this.refreshToken = token;
        this.expiresAt = expiresAt;
    }
}
