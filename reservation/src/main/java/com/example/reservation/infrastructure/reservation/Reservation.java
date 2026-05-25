package com.example.reservation.infrastructure.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.reservation.infrastructure.resource.Resource;
import com.example.reservation.infrastructure.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Reservation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    User user;
    @ManyToOne(fetch=FetchType.LAZY)
    Resource resource;

    public Reservation(LocalDate date, LocalTime startTime, LocalTime endTime,
        User user, Resource resource) {
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.user = user;
            this.resource = resource;
    }
}
