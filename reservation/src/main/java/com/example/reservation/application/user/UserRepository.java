package com.example.reservation.application.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.reservation.domain.user.UserDomain;

@Repository
public interface UserRepository {
  public UserDomain save(UserDomain doamin);
  public UserDomain findById(Long id);
  public UserDomain findByUsername(String username);
  public UserDomain findByProviderAndProviderId(String provider, String providerId);
  public List<UserDomain> findAll();
  public void deleteByUsername(String username);
  public boolean existsByEmail(String email);
}
