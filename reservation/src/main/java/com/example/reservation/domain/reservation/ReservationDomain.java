package com.example.reservation.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationDomain {
  private Long id;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private Long userId;
  private Long resourceId;

  public void changeDate(LocalDate newDate) {
    this.date = newDate;
  }

  public void changeTime(LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public void changeResource(Long resourceId) {
    this.resourceId = resourceId;
  }
}
