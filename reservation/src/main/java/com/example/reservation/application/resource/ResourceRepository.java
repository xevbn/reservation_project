package com.example.reservation.application.resource;

import java.util.Optional;

import com.example.reservation.domain.ResourceDomain;

public interface ResourceRepository {
  public ResourceDomain save(ResourceDomain resource);
  public Optional<ResourceDomain> findById(long id);
  public Optional<ResourceDomain> findByName(String name);
  public void deleteById(long id);
}
