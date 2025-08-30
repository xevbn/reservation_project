package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ResourceRepository resourceRepository;

    //예약 작성
    public Reservation makeReservation(ReservationDto reservationDto, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new EntityNotFoundException("해당 리소스를 찾을 수 없습니다. " + resourceId));
        LocalDate date = reservationDto.getDate();
        LocalDateTime start = reservationDto.getStartTime();
        LocalDateTime end = reservationDto.getEndTime();

        boolean overlaps = reservationRepository.existsOverlap(resource, start, end);
            
        if(overlaps) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음 " + username));

        Reservation newReservation = new Reservation();

        newReservation.setDate(date);
        newReservation.setStartTime(start);
        newReservation.setEndTime(end);
        newReservation.setUser(user);
        newReservation.setResource(resource);

        return reservationRepository.save(newReservation);
    }

    //일별 예약
    public List<Reservation> findReservationByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    //사용자 별 예약 확인
    public List<Reservation> findReservationByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + username));

        return reservationRepository.findByUser(user);
    }

    //예약 취소
    public void cancelReservation(LocalDate date, LocalDateTime startTime) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + username));

        Reservation reservation = reservationRepository.findByUserAndStartTimeAndDate(user, startTime, date)
            .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));
        
        //작성자와 현재 사용자 일치 확인
        //이 코드 의미 없는데
        if (!user.equals(reservation.getUser())) {
            throw new AccessDeniedException("사용자가 아닙니다.");
        }

        reservationRepository.deleteByUserAndStartTimeAndDate(user, startTime, date);
    }

    //예약 변경
    public void changeReservation(LocalDate date, LocalDateTime startTime, ReservationDto reservationDto, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new EntityNotFoundException("해당 리소스를 찾을 수 없습니다. " + resourceId));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + username));

        Reservation reservation = reservationRepository.findByUserAndStartTimeAndDate(user, startTime, date)
            .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다. " + user + "_" + startTime));

        //작성자와 현재 사용자 일치 확인
        if (!user.equals(reservation.getUser())) {
            throw new AccessDeniedException("사용자가 아닙니다.");
        }

        LocalDate newDate = reservationDto.getDate();
        LocalDateTime newStartTime = reservationDto.getStartTime();
        LocalDateTime newEndTime = reservationDto.getEndTime();

        //변경 사항이 없을 때는 예외 발생
        if (reservation.getDate().isEqual(newDate) && 
            reservation.getStartTime().isEqual(newStartTime) &&
            reservation.getEndTime().isEqual(newEndTime)) {
                throw new IllegalArgumentException("변경 사항이 없습니다.");
            }

        boolean overlaps = reservationRepository.existsOverlap(resource, newStartTime, newEndTime);

        if(overlaps) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }
        
        reservation.setDate(newDate);
        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
    }

    public Iterable<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public void deleteAll() {
        reservationRepository.deleteAll();
    }
}
