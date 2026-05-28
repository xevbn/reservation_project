package com.example.reservation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResourceDomain {
  private long id;
  private String name;
  
  public ResourceDomain(String name) {
    this.name = name;
  }

  public void changeName(String newName) {
    this.name = newName;
  }
}
