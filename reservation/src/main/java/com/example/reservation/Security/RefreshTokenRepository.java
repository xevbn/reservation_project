package com.example.reservation.Security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    public Optional<RefreshToken> findByUsername(String username);
    public Optional<RefreshToken> findByUserId(Long userId);
}
