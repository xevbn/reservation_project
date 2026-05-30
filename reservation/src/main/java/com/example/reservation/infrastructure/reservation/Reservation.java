package com.example.reservation.infrastructure.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumn(name="resource_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Resource resource;

    public String getReservationTime() {
        String time = this.date.toString() + ":" + this.startTime.toString() + this.endTime.toString();
        return time;
    }
}
