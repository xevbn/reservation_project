package com.example.reservation.application.user;

import java.util.List;
import java.util.Optional;

import com.example.reservation.domain.UserDomain;

public interface UserRepository {
  public Optional<UserDomain> findById(long id);
  public UserDomain save(UserDomain user);
  public void deleteById(long id);
  public Optional<UserDomain> findByUsername(String username);
  public boolean existsByUsername(String username);
  public boolean existsByEmail(String email);
  public List<UserDomain> findAll();
}
