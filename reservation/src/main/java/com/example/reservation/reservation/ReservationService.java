package com.example.reservation.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.resource.Resource;
import com.example.reservation.resource.ResourceRepository;
import com.example.reservation.user.User;
import com.example.reservation.user.UserService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ResourceRepository resourceRepository;
    private final SseService sseService;

    //예약 작성
    public Reservation makeReservation(ReservationDto reservationDto) {
        Long resourceId = reservationDto.getResourceId();
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
            
        LocalDate date = reservationDto.getDate();
        LocalTime start = reservationDto.getStartTime();
        LocalTime end = reservationDto.getEndTime();

        //중복된 예약이 있을 시
        boolean overlaps = reservationRepository.existsOverlap(resource, start, end, date);
        if(overlaps) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Reservation newReservation = new Reservation();

        newReservation.setDate(date);
        newReservation.setStartTime(start);
        newReservation.setEndTime(end);
        newReservation.setUser(user);
        newReservation.setResource(resource);

        Reservation reserved = reservationRepository.save(newReservation);

        Map<String, Boolean> occupied = getReservedList(date, resource.getId());
        sseService.sendUpdate(date, resource.getId(), occupied);

        return reserved;
    }

    //일별 예약
    public List<Reservation> findReservationByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    //사용자 별 예약 확인
    public List<Reservation> findReservationByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return reservationRepository.findByUser(user);
    }

    //예약 취소
    public void cancelReservation(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        
        //작성자와 현재 사용자 일치 확인
        if (!user.equals(reservation.getUser())) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        reservationRepository.deleteById(id);
    }

    //예약 변경
    public void changeReservation(Long id, ReservationDto reservationDto) {
        Long resourceId = reservationDto.getResourceId();
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        //작성자와 현재 사용자 일치 확인
        if (!user.equals(reservation.getUser())) {
            throw new BusinessException(ErrorCode.NOT_SAME_USER);
        }

        LocalDate newDate = reservationDto.getDate();
        LocalTime newStartTime = reservationDto.getStartTime();
        LocalTime newEndTime = reservationDto.getEndTime();

        //변경 사항이 없을 때는 예외 발생
        if (reservation.getDate().isEqual(newDate) && 
            reservation.getStartTime().equals(newStartTime) &&
            reservation.getEndTime().equals(newEndTime)) {
                throw new BusinessException(ErrorCode.NO_CHANGE_FOUND);
            }

        boolean overlaps = reservationRepository.existsOverlap(resource, newStartTime, newEndTime, newDate);

        if(overlaps) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }
        
        reservation.setDate(newDate);
        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
    }

    //전체 예약 리스트 반환
    public Iterable<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    //전체 예약 삭제
    public void deleteAll() {
        reservationRepository.deleteAll();
    }

    //일자 및 리소스 id를 통해 예약 리스트 반환
    //이거 도대체 왜 있음????? 반환 없는데???
    public void getReservationsByDateAndResourceId(LocalDate date, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        List<Reservation> reservations = reservationRepository.findByDateAndResource(date, resource);

        
    }

    //id를 통해 해당 예약에 접근
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    //해당 시간대가 점유 중임을 확인하기 위한 시간대-부울 반환
    public Map<String, Boolean> getReservedList(LocalDate date, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        List<Reservation> reserved = reservationRepository.findByDateAndResource(date, resource);

        List<LocalTime> reservedTime;
        reservedTime = reserved.stream()
                .map(Reservation::getStartTime)
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
