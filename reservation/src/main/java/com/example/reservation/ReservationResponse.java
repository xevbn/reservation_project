package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponse {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String username;

    public ReservationResponse(Reservation reservation) {
        this.date = reservation.getDate();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.username = reservation.getUser().getUsername();
    }
}
