package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;




@Controller
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("MM.dd")
        .parseDefaulting(java.time.temporal.ChronoField.YEAR, LocalDate.now().getYear())
        .toFormatter();
    private final ObjectMapper objMapper;

    //응답 어떻게 할건지 생각해두기 응답에 넣기? form 써서 넘기기?
    @GetMapping("/{date}")
    public ResponseEntity<String> getSelectedDatReservation(@PathVariable String date) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        List<Reservation> reservList = reservationService.findReservationByDate(selectedDate);
        String responseBody;

        try {
            responseBody = objMapper.writeValueAsString(reservList);
        } catch (JsonProcessingException e) {
            System.out.println("직렬화 오류 발생" + e);
            return ResponseEntity.internalServerError().body("error: 서버 내부 오류");
        }

        return ResponseEntity.ok(responseBody);
    }
    
    @PostMapping("/{date}")
    public void makeReservation(@PathVariable String date, @RequestParam ReservationDto reservationDto,
        @RequestParam Long resourceId) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        reservationService.makeReservation(reservationDto, resourceId);
    }
    
    @DeleteMapping("/{date}/{time}")
    public void cancelReservation(@PathVariable String date, @PathVariable LocalDateTime startTime) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        reservationService.cancelReservation(selectedDate, startTime);
    }

    @PutMapping("/{date}/{time}")
    public void changeReservation(@PathVariable String date, @RequestParam ReservationDto reservationDto, @RequestParam LocalDateTime startTime,
        Long resourceId) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        reservationService.changeReservation(selectedDate, startTime, reservationDto, resourceId);
    }

    @GetMapping("/reservationDetail")
    public ResponseEntity<String> getUsersReservations() {
        List<Reservation> reservList = reservationService.findReservationByUser();
        String reservationList;

        try {
            reservationList = objMapper.writeValueAsString(reservList);
        } catch (JsonProcessingException e) {
            System.out.println("직렬화 오류 발생" + e);
            return ResponseEntity.internalServerError().body("error: 서버 내부 오류");
        }

        return ResponseEntity.ok(reservationList);
    }
    
}
