package com.example.reservation.presentation.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.reservation.domain.ReservationDomain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private long username;
    private long resourceId;

    public ReservationResponse(ReservationDomain reservation) {
        this.id = reservation.getId();
        this.date = reservation.getDate();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.username = reservation.getUserId();
        this.resourceId = reservation.getResourceId();
    }
}
