package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservation.application.reservation.ReservationService;
import com.example.reservation.domain.reservation.ReservationDomain;
import com.example.reservation.infrastructure.reservation.Reservation;
import com.example.reservation.infrastructure.resource.Resource;
import com.example.reservation.infrastructure.resource.ResourceJpaRepository;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.infrastructure.user.UserJpaRepository;
import com.example.reservation.presentation.reservation.ReservationDto;

import jakarta.persistence.EntityNotFoundException;


@SpringBootTest
@DirtiesContext
public class ReservationServiceTest {
    @Autowired
    UserJpaRepository userRepository;
    @Autowired
    ReservationService reservationService;
    @Autowired
    ResourceJpaRepository resourceRepository;
    Long resourceId;

    Long id;

    public void makeAuth(int num) {
        User testUser = new User();
        testUser.setUsername("username" + num);
        testUser.setPassword("password");
        testUser.setEmail("email" + num);
        testUser.setUserRole("USER");

        id = userRepository.save(testUser).getId();
        userRepository.findByUsername("username" + num);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUser.getUsername(), testUser.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeEach
    public void setUp() {
        Resource resource = new Resource("room13");
        resourceRepository.saveAndFlush(resource);
        resourceId = resource.getId();

        makeAuth(10);
    }

    @AfterEach
    public void tearDown() {
        reservationService.deleteAll();
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now();
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(16, 00));
        reservDto.setEndTime(LocalTime.of(17, 00));
        reservDto.setResourceId(resourceId);

        reservationService.makeReservation(
            date,
            reservDto.getStartTime(),
            reservDto.getEndTime(),
            id,
            resourceId
        );

        assertThat(reservationService.findAll()).hasSize(1);
    }

    @Test
    public void makeReservationAtAlreadyAssigned() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now();
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(16, 00));
        reservDto.setEndTime(LocalTime.of(17, 00));
        reservDto.setResourceId(resourceId);

        reservationService.makeReservation(
            date,
            reservDto.getStartTime(),
            reservDto.getEndTime(),
            id,
            resourceId
        );

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDate(date);
        reservationDto.setStartTime(LocalTime.of(16, 00));
        reservationDto.setEndTime(LocalTime.of(18, 00));
        reservationDto.setResourceId(resourceId);

        assertThrows(IllegalStateException.class,
            () -> reservationService.makeReservation(
                date,
                reservationDto.getStartTime(),
                reservationDto.getEndTime(),
                id,
                resourceId
            ));
    }

    //이런 방식의 테스트는 맞지만 h2 환경에서는 부적합 - 테스트 불가
    @Test
    public void makeMultipleReservationAtSameTime() throws Exception {
        LocalDate date = LocalDate.now();
        ReservationDto reservDto = new ReservationDto();
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(18, 0));
        reservDto.setEndTime(LocalTime.of(19, 0));
        reservDto.setResourceId(resourceId);

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int num = i;
            executorService.submit(() -> {
                try {
                    makeAuth(num);
                    reservationService.makeReservation(
                        reservDto.getDate(),
                        reservDto.getStartTime(),
                        reservDto.getEndTime(),
                        id,
                        reservDto.getResourceId()
                    );  
                    System.out.println("스레드 " + num + "예약 성공");
                } catch (Exception e) {
                    System.out.println("예약 실패 : " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        List<ReservationDomain> reservList = new ArrayList<>();
        reservationService.findAll().stream().forEach(reservList::add);  
        
        assertThat(reservList).hasSize(1);
    }

    @Test
    public void cancelNonExistingReservation() throws Exception{
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.of(0, 0);
        
        assertThrows(EntityNotFoundException.class,
        () -> reservationService.cancelReservation(id));
    }

    @Test
    @Transactional
    public void cancelReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(16, 00));
        reservDto.setEndTime(LocalTime.of(17, 00));
        reservDto.setResourceId(resourceId);

        Long reservId = reservationService.makeReservation(
            date,
            reservDto.getStartTime(),
            reservDto.getEndTime(),
            id,
            resourceId
        ).getId();

        reservationService.cancelReservation(reservId);

        assertThat(reservationService.findAll()).hasSize(0);
    }

    @Test
    public void tryToCancelAnotherUsersReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(16, 00));
        reservDto.setEndTime(LocalTime.of(17, 00));
        reservDto.setResourceId(resourceId);

        Long thisReservId = reservationService.makeReservation(
            reservDto.getDate(),
            reservDto.getStartTime(),
            reservDto.getEndTime(),
            id,
            resourceId
        ).getId();

        makeAuth(12);
        assertThrows(EntityNotFoundException.class,
            () -> reservationService.cancelReservation(thisReservId));
    }

    @Test
    public void changeWrongReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalTime.of(16, 00));
        reservDto.setEndTime(LocalTime.of(17, 00));
        reservDto.setResourceId(resourceId);

        assertThrows(EntityNotFoundException.class,
            () -> reservationService.changeReservation(id, resourceId, date, reservDto.getStartTime(), reservDto.getEndTime()));
    }

    @Test
    @Transactional
    public void changeToAlreadyAssignedTime_shouldThrowException() {
        LocalDate date = LocalDate.now();

        // 1번 예약 16-17
        ReservationDto dto1 = new ReservationDto(date,
            LocalTime.of(16, 0),
            LocalTime.of(17, 0),
            resourceId
        );
        ReservationDomain r1 = reservationService.makeReservation(
            date,
            dto1.getStartTime(),
            dto1.getEndTime(),
            id,
            resourceId
        );

        // 2번 예약 17-18
        ReservationDto dto2 = new ReservationDto(date,
            LocalTime.of(17, 0),
            LocalTime.of(18, 0),
            resourceId
        );
        ReservationDomain r2 = reservationService.makeReservation(
            date,
            dto2.getStartTime(),
            dto2.getEndTime(),
            id,
            resourceId
        );

        // 2번 예약을 16-19로 변경 시도
        ReservationDto changeDto = new ReservationDto(date,
            LocalTime.of(16, 0),
            LocalTime.of(19, 0),
            resourceId
        );

        assertThrows(IllegalStateException.class, () -> {
            reservationService.changeReservation(r2.getId(), resourceId, date, changeDto.getStartTime(), changeDto.getEndTime());
        });
    }

    @Test
    @Transactional
    public void changeReservation() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(16, 00);
        LocalTime end = LocalTime.of(17, 00);
        ReservationDto dto = new ReservationDto(date, start, end, resourceId);

        Long reservId = reservationService.makeReservation(
            date,
            start,
            end,
            id,
            resourceId
        ).getId();

        ReservationDto change = new ReservationDto(
            date,
            LocalTime.of(18, 0),
            LocalTime.of(19, 0),
            resourceId
        );

        reservationService.changeReservation(reservId, resourceId, date, change.getStartTime(), change.getEndTime());
        
        ReservationDomain reservation = reservationService.findAll().iterator().next();
        assertThat(reservation.getStartTime().equals(LocalTime.of(18, 0)) &&
            reservation.getEndTime().equals(LocalTime.of(19, 0)));
    }

    @Test
    @Transactional
    public void tryToChangeAnotherUsersReservation() throws Exception{
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(16, 00);
        LocalTime end =  LocalTime.of(17, 00);
        ReservationDto dto = new ReservationDto(date, start, end, resourceId);

        Long anotherUsersReservId = reservationService.makeReservation(
            date,
            start,
            end,
            id,
            resourceId
        ).getId();

        makeAuth(11);

        ReservationDto change = new ReservationDto(
            date,
            LocalTime.of(18, 0),
            LocalTime.of(19, 0),
            resourceId
        );

        assertThrows(EntityNotFoundException.class,
            () -> reservationService.changeReservation(anotherUsersReservId, resourceId, date, change.getStartTime(), change.getEndTime()));
    }
}
