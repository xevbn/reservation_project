package com.example.reservation.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationDomain {
  private long id;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private long userId;
  private long resourceId;

  public ReservationDomain(LocalDate date, LocalTime startTime, LocalTime endTime, long resourceId, long userId) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.resourceId = resourceId;
    this.userId = userId;
  }

  public void changeDate(LocalDate newDate) {
    this.date = newDate;
  }

  public void changeStartTime(LocalTime newStartTime) {
    this.startTime = newStartTime;
  }

  public void changeEndTime(LocalTime newEndTime) {
    this.endTime = newEndTime;
  }

  public void changeResourceId(long newResourceId) {
    this.resourceId = newResourceId;
  }

  public String getReservationTime() {
    return date.toString() + " " + startTime.toString() + " - " + endTime.toString();
  }
}
