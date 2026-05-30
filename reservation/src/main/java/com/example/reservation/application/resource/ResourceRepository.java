package com.example.reservation.application.resource;

import java.util.List;
import java.util.Optional;

import com.example.reservation.domain.ResourceDomain;

public interface ResourceRepository {
  public ResourceDomain save(ResourceDomain resource);
  public Optional<ResourceDomain> findById(long id);
  public Optional<ResourceDomain> findByName(String name);
  public void deleteById(long id);
  public List<ResourceDomain> findAll();
}
