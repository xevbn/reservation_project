package com.example.reservation.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;




@RestController
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final SseService sseService;
    private final static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("M.d")
        .parseDefaulting(java.time.temporal.ChronoField.YEAR, LocalDate.now().getYear())
        .toFormatter();
    private final ObjectMapper objMapper = new ObjectMapper();

    //애초에 url을 어떻게 설정한건지도 문제인데
    //응답 어떻게 할건지 생각해두기 응답에 넣기? form 써서 넘기기?
    @GetMapping("/{date}")  //이거 바꿔야하는거 아닌가 몰라
    public ResponseEntity<?> getSelectedDatReservation(@PathVariable LocalDate date, @PathParam(value = "resourceId") Long resourceId) {
        Map<String, Boolean> occupied = reservationService.getReservedList(date, resourceId);

        return ResponseEntity.ok(occupied);
    }
    
    //예약 추가
    @PostMapping("/{date}")
    public ResponseEntity<?> makeReservation(@PathVariable LocalDate date, @RequestBody ReservationDto reservationDto) {
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

    //해당 일자 및 리소스에 대한 시간대 점유 여부 sse 구독 엔드포인트
    @GetMapping(value="/{resourceId}/sse", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long resourceId, @PathParam(value="date") LocalDate date) throws JsonProcessingException {
        SseEmitter sseEmitter = sseService.subscribe(date, resourceId);

        //구독 시 바로 해당 시점의 점유 리스트 반환
        Map<String, Boolean> occupied = reservationService.getReservedList(date, resourceId);
        sseService.sendUpdate(date, resourceId, occupied);

        return sseEmitter;
    }
    
}
