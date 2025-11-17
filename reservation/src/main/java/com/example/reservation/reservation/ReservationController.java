package com.example.reservation.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
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
    private final ObjectMapper objMapper = new ObjectMapper();

    //애초에 url을 어떻게 설정한건지도 문제인데
    //응답 어떻게 할건지 생각해두기 응답에 넣기? form 써서 넘기기?
    @GetMapping("/{date}")
    public ResponseEntity<?> getSelectedDatReservation(@PathVariable String date) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);

        List<ReservationResponse> body = reservationService.findReservationByDate(selectedDate)
            .stream()
            .map(ReservationResponse::new)
            .toList();

        return ResponseEntity.ok(body);
    }
    
    //예약 추가
    @PostMapping("/{date}")
    public ResponseEntity<?> makeReservation(@PathVariable String date, @RequestBody ReservationDto reservationDto) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        
        ReservationResponse resBody = new ReservationResponse(reservationService.makeReservation(reservationDto));

        return ResponseEntity.ok(resBody);
    }

    //해당 예약 정보 접근 엔드포인트
    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getReservationDetail(@PathVariable String id) {
        Reservation reservation = reservationService.getReservationById(Long.valueOf(id))
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        ReservationResponse reservationResponse = new ReservationResponse(reservation);

        return ResponseEntity.ok(reservationResponse);
    }
    

    //해당 예약 취소
    @DeleteMapping("/{id}/detail")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);

        return ResponseEntity.status(HttpStatusCode.valueOf(204)).build();
    }

    //예약 내역 수정
    @PutMapping("/{id}/detail")
    public ResponseEntity<?> changeReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto) {
        reservationService.changeReservation(id, reservationDto);

        return ResponseEntity.status(HttpStatusCode.valueOf(204)).build();
    }

    //해당 사용자의 예약 사항 리턴
    @GetMapping("/detail")
    public ResponseEntity<?> getUsersReservations() {
        List<ReservationResponse> resBody =  reservationService.findReservationByUser()
            .stream()
            .map(ReservationResponse::new)
            .toList();
        
        return ResponseEntity.ok(resBody);
    }

    @GetMapping("/{date}/reservedList")
    public ResponseEntity<?> getMethodName(@PathVariable String date) {
        LocalDate selectedDate = LocalDate.parse(date, formatter);
        List<Boolean> reservedList = reservationService.getReservedList(selectedDate);

        Map<String, List<Boolean>> reservedMap = Map.of("reservedList", reservedList);

        return ResponseEntity.ok(reservedMap);
    }
    
}
