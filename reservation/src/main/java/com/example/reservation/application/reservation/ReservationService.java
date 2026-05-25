package com.example.reservation.application.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.reservation.application.resource.ResourceService;
import com.example.reservation.application.user.UserService;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.reservation.ReservationDomain;
import com.example.reservation.domain.resource.ResourceDomain;
import com.example.reservation.domain.user.UserDomain;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ResourceService resourceService;
    private final SseService sseService;

    //예약 작성
    public ReservationDomain makeReservation(LocalDate date, LocalTime startTime, LocalTime endTime,
        Long userId, Long resourceId) {

        resourceService.getResource(resourceId);

        //중복된 예약이 있을 시
        boolean overlaps = reservationRepository.existsOverlap(resourceId, startTime, endTime, date);
        if(overlaps) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        
        ReservationDomain reservationDomain = new ReservationDomain(
          null, date, startTime, endTime, userId, resourceId);

        ReservationDomain reserved = reservationRepository.save(reservationDomain);

        Map<String, Boolean> occupied = getReservedList(date, resourceId);
        sseService.sendUpdate(date, resourceId, occupied);

        return reserved;
    }

    //일별 예약
    public List<ReservationDomain> findReservationByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    //사용자 별 예약 확인
    public List<ReservationDomain> findReservationByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDomain user = userService.findByUsername(username);

        return reservationRepository.findByUser(user.getId());
    }

    //예약 취소
    public void cancelReservation(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDomain user = userService.findByUsername(username);

        ReservationDomain reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        //작성자와 현재 사용자 일치 확인
        if (!user.getId().equals(reservation.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        reservationRepository.deleteById(id);
    }

    //예약 변경
    public void changeReservation(Long id, Long resourceId, LocalDate newDate, LocalTime newStart, LocalTime newEnd) {
        ResourceDomain resource = resourceService.getResource(resourceId);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDomain user = userService.findByUsername(username);

        ReservationDomain reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        //작성자와 현재 사용자 일치 확인
        if (!user.getId().equals(reservation.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        //변경 사항이 없을 때는 예외 발생
        if (reservation.getDate().isEqual(newDate) && 
            reservation.getStartTime().equals(newStart) &&
            reservation.getEndTime().equals(newEnd)) {
                throw new BusinessException(ErrorCode.NO_CHANGE_FOUND);
            }

        boolean overlaps = reservationRepository.existsOverlap(resource.getId(), newStart, newEnd, newDate);

        if(overlaps) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }
        
        reservation.changeDate(newDate);
        reservation.changeTime(newStart, newEnd);
    }

    //전체 예약 리스트 반환
    public List<ReservationDomain> findAll() {
        return reservationRepository.findAll();
    }

    //전체 예약 삭제
    public void deleteAll() {
        reservationRepository.deleteAll();
    }

    //id를 통해 해당 예약에 접근
    public Optional<ReservationDomain> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    //해당 시간대가 점유 중임을 확인하기 위한 시간대-부울 반환
    public Map<String, Boolean> getReservedList(LocalDate date, Long resourceId) {
        ResourceDomain resource = resourceService.getResource(resourceId);

        List<ReservationDomain> reserved = 
          reservationRepository.findByDateAndResource(date, resource.getId());

        List<LocalTime> reservedTime;
        reservedTime = reserved.stream()
                .map(ReservationDomain::getStartTime)
                .toList();
        
        Map<String, Boolean> timeList = new TreeMap<>();
        for(int hour = 9; hour < 18; hour++) {
            LocalTime slot = LocalTime.of(hour, 0);
            boolean isReserved = reservedTime.contains(slot);
            timeList.put(LocalTime.of(hour, 0).toString() + "-" + LocalTime.of(hour + 1, 0),
                isReserved);
        }

        return timeList;
    }
}
