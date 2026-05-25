package com.example.reservation.infrastructure.user;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Repository;

import com.example.reservation.application.user.UserRepository;
import com.example.reservation.domain.user.UserDomain;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
  private final UserJpaRepository jpaRepository;

  @Override
  public UserDomain save(UserDomain domain) {
    User user = UserMapper.toEntity(domain);

    User saved = jpaRepository.save(user);
    return UserMapper.toDomain(saved);
  }

  @Override
  public UserDomain findById(Long id) {
    User user = jpaRepository.findById(id)
      .orElse(null);

    return UserMapper.toDomain(user);
  }

  @Override
  public UserDomain findByUsername(String username) {
    User user = jpaRepository.findByUsername(username)
      .orElse(null);

    return UserMapper.toDomain(user);
  }

  @Override
  public UserDomain findByProviderAndProviderId(String provider, String providerId) {
    User user = jpaRepository.findByProviderAndProviderId(provider, providerId)
      .orElse(null);

    return UserMapper.toDomain(user);
  }

  @Override
  public List<UserDomain> findAll() {
    return jpaRepository.findAll().stream()
      .map(UserMapper::toDomain)
      .toList();
  }

  @Override
  public void deleteByUsername(String username) {
    jpaRepository.deleteByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }
}
