package com.example.reservation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResourceDomain {
  private long id;
  private String name;
  private List<Long> reservations;
  
  public ResourceDomain(String name) {
    this.name = name;
    this.reservations = new ArrayList<>();
  }

  public void changeName(String newName) {
    this.name = newName;
  }

  public List<Long> getReservations() {
    return this.reservations;
  }
}
