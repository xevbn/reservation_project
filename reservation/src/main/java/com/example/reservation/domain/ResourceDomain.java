package com.example.reservation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    reservations = new ArrayList<>();
  }

  public void changeName(String newName) {
    this.name = newName;
  }

  public void addReservation(long reservationId) {
    reservations.add(reservationId);
  }

  public void removeReservation(long reservationId) {
    reservations.remove(reservationId);
  }

  public List<Long> getReservations() {
    return reservations;
  }
}
