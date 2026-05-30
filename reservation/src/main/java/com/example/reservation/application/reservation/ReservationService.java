package com.example.reservation.application.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.reservation.application.resource.ResourceRepository;
import com.example.reservation.application.user.UserRepository;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.ReservationDomain;
import com.example.reservation.domain.UserDomain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //예약 작성
    public ReservationDomain makeReservation(LocalDate date, LocalTime start, LocalTime end, long resourceId, long userId) {
        resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
            
        String reservationTime = date.toString() + "|" + start.toString() + end.toString();
        
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        log.info("Reservation [생성 시도] - user: {}, resource: {}, time: {}", userId, resourceId, reservationTime);

        //중복된 예약이 있을 시
        boolean overlaps = reservationRepository.existsOverlap(resourceId, start, end, date);
        if(overlaps) {
            log.warn("Reservation [중복 발생] - 이미 점유된 시간대입니다");
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }

        ReservationDomain newReservation = new ReservationDomain(
            date,
            start,
            end,
            resourceId,
            user.getId()
        );

        ReservationDomain reserved = reservationRepository.save(newReservation);
        publishUpdate("ADD", date, resourceId);
        log.info("Reservation [생성 완료] - ID : {}", reserved.getId());

        return reserved;
    }

    //일별 예약
    public List<ReservationDomain> findReservationByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    //사용자 별 예약 확인
    public List<ReservationDomain> findReservationByUser(long userId) {
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return reservationRepository.findByUserId(user.getId());
    }

    //예약 취소
    public void cancelReservation(Long id, Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ReservationDomain reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        //작성자와 현재 사용자 일치 확인
        if (userId != reservation.getUserId()) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        publishUpdate("DELETE", reservation.getDate(), reservation.getResourceId());
        reservationRepository.deleteById(id);
        log.info("Reservation [삭제] - id: {}", id);
    }

    //예약 변경
    public ReservationDomain changeReservation(Long id, LocalDate newDate, LocalTime newStart, LocalTime newEnd, 
        Long resourceId, Long userId) {
        log.info("Reservation [변경 시도] - id: {}", id);
        resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ReservationDomain reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        String prevTime = reservation.getReservationTime();

        //작성자와 현재 사용자 일치 확인
        if (userId != reservation.getUserId()) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        LocalDate prevDate = reservation.getDate();
        Long prevResourceId = reservation.getResourceId();

        //변경 사항이 없을 때는 예외 발생
        if (reservation.getDate().isEqual(newDate) && 
            reservation.getStartTime().equals(newStart) &&
            reservation.getEndTime().equals(newEnd)) {
                throw new BusinessException(ErrorCode.NO_CHANGE_FOUND);
            }

        boolean overlaps = reservationRepository.existsOverlap(resourceId, newStart, newEnd, newDate);

        if(overlaps) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }
        
        reservation.changeDate(newDate);
        reservation.changeStartTime(newStart);
        reservation.changeEndTime(newEnd);

        publishUpdate("DELETE", prevDate, prevResourceId);
        publishUpdate("ADD", newDate, resourceId);

        log.info("Reservation [변경 성공] - id: {}, from: {}, to: {}", id, prevTime, reservation.getReservationTime());

        return reservation;
    }

    //전체 예약 리스트 반환
    public List<ReservationDomain> findAll() {
        return reservationRepository.findAll();
    }

    //전체 예약 삭제
    public void deleteAll() {
        reservationRepository.deleteAll();
    }

    //일자 및 리소스 id를 통해 예약 리스트 반환
    //이거 도대체 왜 있음????? 반환 없는데???
    public List<ReservationDomain> getReservationsByDateAndResourceId(LocalDate date, Long resourceId) {
        resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        List<ReservationDomain> reservations = reservationRepository.findByDateAndResourceId(date, resourceId);

        return reservations;
    }

    //id를 통해 해당 예약에 접근
    public ReservationDomain getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    //해당 시간대가 점유 중임을 확인하기 위한 시간대-부울 반환
    public Map<String, Boolean> getReservedList(LocalDate date, Long resourceId) {
        resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        List<ReservationDomain> reserved = reservationRepository.findByDateAndResourceId(date, resourceId);

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

    public void publishUpdate(String action, LocalDate date, Long resourceId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String payLoad = String.format("{\"action\":\"%s\",\"date\":\"%s\",\"resourceId\":%d}",
            action, date.format(formatter), resourceId);
        redisTemplate.convertAndSend("SLOT_UPDATE", payLoad);
    }
}
