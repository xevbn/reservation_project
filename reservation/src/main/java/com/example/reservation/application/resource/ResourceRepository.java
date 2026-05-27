package com.example.reservation.application.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.reservation.domain.resource.ResourceDomain;

public interface ResourceRepository {
  public ResourceDomain save(ResourceDomain domain);
  public List<ResourceDomain> findAll();
  public Optional<ResourceDomain> findById(Long id);
  public Optional<ResourceDomain> findByName(String name);
  public void deleteById(Long id);
}
