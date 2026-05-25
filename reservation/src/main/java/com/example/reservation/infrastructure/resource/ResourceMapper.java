package com.example.reservation.infrastructure.resource;

import com.example.reservation.domain.resource.ResourceDomain;

public class ResourceMapper {
  public static ResourceDomain toDomain(Resource resource) {
    return new ResourceDomain(
      resource.getId(),
      resource.getName()
    );
  }

  public static Resource toEntity(ResourceDomain domain) {
    return new Resource(
      domain.getId(), 
      domain.getName()
    );
  }
}
