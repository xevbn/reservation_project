package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("M.d")
        .parseDefaulting(java.time.temporal.ChronoField.YEAR, LocalDate.now().getYear())
        .toFormatter();
    private final ObjectMapper objMapper;

    //응답 어떻게 할건지 생각해두기 응답에 넣기? form 써서 넘기기?
    @GetMapping("/{date}")
    public List<ReservationResponse> getSelectedDatReservation(@PathVariable String date) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);

        return reservationService.findReservationByDate(selectedDate)
            .stream()
            .map(ReservationResponse::new)
            .toList();
    }
    
    @PostMapping("/{date}")
    public Reservation makeReservation(@PathVariable String date, @RequestBody ReservationDto reservationDto) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        
        return reservationService.makeReservation(reservationDto);
    }
    
    @DeleteMapping("/{date}/{time}")
    public void cancelReservation(@PathVariable String date, @PathVariable LocalTime time) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        reservationService.cancelReservation(selectedDate, time);
    }

    @PutMapping("/{date}/{time}")
    public void changeReservation(@PathVariable String date, @RequestBody ReservationDto reservationDto, @PathVariable LocalTime time) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        reservationService.changeReservation(selectedDate, time, reservationDto);
    }

    @GetMapping("/detail")
    public List<ReservationResponse> getUsersReservations() {
        return reservationService.findReservationByUser()
            .stream()
            .map(ReservationResponse::new)
            .toList();
    }
}
