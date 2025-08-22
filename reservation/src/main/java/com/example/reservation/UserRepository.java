package com.example.reservation;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface  UserRepository extends CrudRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    //oauth 추가 시 사용
    public Optional<User> findByProviderAndProviderId(String provider, String providerId);
    public void deleteByUsername(String username);
}
