package com.example.reservation.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResourceDomain {
  private Long id;
  private String name;

  public ResourceDomain(String name) {
    this.id = null;
    this.name = name;
  }

  public ResourceDomain create(String name) {
    return new ResourceDomain(name);
  }

  public void changeName(String name) {
    this.name = name;
  }
}
