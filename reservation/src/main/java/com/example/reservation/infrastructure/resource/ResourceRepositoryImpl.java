package com.example.reservation.infrastructure.resource;

import java.util.List;
import java.util.Optional;

import com.example.reservation.application.resource.ResourceRepository;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.resource.ResourceDomain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResourceRepositoryImpl implements ResourceRepository {
  private final ResourceJpaRepository jpaRepository;

  @Override
  public ResourceDomain save(ResourceDomain domain) {
    Resource toSave = ResourceMapper.toEntity(domain);
    Resource saved = jpaRepository.save(toSave);

    return ResourceMapper.toDomain(saved);
  }

    @Override
    public List<ResourceDomain> findAll() {
      return jpaRepository.findAll().stream()
        .map(ResourceMapper::toDomain)
        .toList();
    }

    @Override
    public Optional<ResourceDomain> findById(Long id) {
      return jpaRepository.findById(id)
        .map(ResourceMapper::toDomain);
    }

    @Override
    public Optional<ResourceDomain> findByName(String name) {
      return jpaRepository.findByName(name)
        .map(ResourceMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
      jpaRepository.deleteById(id);
    }
}
