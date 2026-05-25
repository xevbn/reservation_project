package com.example.reservation.infrastructure.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  UserJpaRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    //oauth 추가 시 사용
    public Optional<User> findByProviderAndProviderId(String provider, String providerId);
    public void deleteByUsername(String username);
    public boolean existsByEmail(String email);
}
