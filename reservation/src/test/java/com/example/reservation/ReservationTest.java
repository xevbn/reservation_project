package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import jakarta.persistence.EntityNotFoundException;


@SpringBootTest
@DirtiesContext
public class ReservationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReservationService reservationService;
    @Autowired
    ResourceRepository resourceRepository;
    Long id;

    public void makeAuth(int num) {
        User testUser = new User();
        testUser.setUsername("username" + num);
        testUser.setPassword("password");
        testUser.setEmail("email" + num);
        testUser.setUserRole("USER");

        userRepository.save(testUser);
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
        id = resource.getId();

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
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(17, 00)));

        reservationService.makeReservation(reservDto, id);

        assertThat(reservationService.findAll()).hasSize(1);
    }

    @Test
    public void makeReservationAtAlreadyAssigned() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now();
        reservDto.setDate(date);
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(17, 00)));

        reservationService.makeReservation(reservDto, id);

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setDate(date);
        reservationDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservationDto.setEndTime(LocalDateTime.of(date, LocalTime.of(18, 00)));

        assertThrows(IllegalStateException.class,
            () -> reservationService.makeReservation(reservationDto, id));
    }

    //이런 방식의 테스트는 맞지만 h2 환경에서는 부적합 - 테스트 불가
    @Test
    public void makeMultipleReservationAtSameTime() throws Exception {
        LocalDate date = LocalDate.now();
        ReservationDto reservDto = new ReservationDto();
        reservDto.setDate(date);
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(18, 0)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(19, 0)));

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int num = i;
            executorService.submit(() -> {
                try {
                    makeAuth(num);
                    reservationService.makeReservation(reservDto, id);
                    System.out.println("스레드 " + num + "예약 성공");
                } catch (Exception e) {
                    System.out.println("예약 실패 : " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        List<Reservation> reservList = new ArrayList<>();
        reservationService.findAll().forEach(reservList::add);
        
        assertThat(reservList).hasSize(1);
    }

    @Test
    public void cancelNonExistingReservation() throws Exception{
        LocalDate date = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(0, 0));
        
        assertThrows(EntityNotFoundException.class,
        () -> reservationService.cancelReservation(date, start));
    }

    @Test
    @Transactional
    public void cancelReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(17, 00)));

        reservationService.makeReservation(reservDto, id);

        reservationService.cancelReservation(date, reservDto.getStartTime());

        assertThat(reservationService.findAll()).hasSize(0);
    }

    @Test
    public void tryToCancelAnotherUsersReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(17, 00)));

        reservationService.makeReservation(reservDto, id);

        makeAuth(12);
        assertThrows(EntityNotFoundException.class,
            () -> reservationService.cancelReservation(date, LocalDateTime.of(date, LocalTime.of(16, 00))));
    }

    @Test
    public void changeWrongReservation() {
        ReservationDto reservDto = new ReservationDto();
        LocalDate date = LocalDate.now().plusDays(1);
        reservDto.setDate(date);
        reservDto.setStartTime(LocalDateTime.of(date, LocalTime.of(16, 00)));
        reservDto.setEndTime(LocalDateTime.of(date, LocalTime.of(17, 00)));

        assertThrows(EntityNotFoundException.class,
            () -> reservationService.changeReservation(date, reservDto.getStartTime(), reservDto, id));
    }

    @Test
    @Transactional
    public void changeToAlreadyAssignedTime_shouldThrowException() {
        LocalDate date = LocalDate.now();

        // 1번 예약 16-17
        ReservationDto dto1 = new ReservationDto(date,
            LocalDateTime.of(date, LocalTime.of(16, 0)),
            LocalDateTime.of(date, LocalTime.of(17, 0))
        );
        Reservation r1 = reservationService.makeReservation(dto1, id);

        // 2번 예약 17-18
        ReservationDto dto2 = new ReservationDto(date,
            LocalDateTime.of(date, LocalTime.of(17, 0)),
            LocalDateTime.of(date, LocalTime.of(18, 0))
        );
        Reservation r2 = reservationService.makeReservation(dto2, id);

        // 2번 예약을 16-19로 변경 시도
        ReservationDto changeDto = new ReservationDto(date,
            LocalDateTime.of(date, LocalTime.of(16, 0)),
            LocalDateTime.of(date, LocalTime.of(19, 0))
        );

        assertThrows(IllegalStateException.class, () -> {
            reservationService.changeReservation(date, r2.getStartTime(), changeDto, id);
        });
    }

    @Test
    @Transactional
    public void changeReservation() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(16, 00));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(17, 00));
        ReservationDto dto = new ReservationDto(date, start, end);

        reservationService.makeReservation(dto, id);

        ReservationDto change = new ReservationDto(
            date,
            LocalDateTime.of(date, LocalTime.of(18, 0)),
            LocalDateTime.of(date, LocalTime.of(19, 0))
        );

        reservationService.changeReservation(date, start, change, id);
        
        Reservation reservation = reservationService.findAll().iterator().next();
        assertThat(reservation.getStartTime().isEqual(LocalDateTime.of(date, LocalTime.of(18, 0))) &&
            reservation.getEndTime().isEqual(LocalDateTime.of(date, LocalTime.of(19, 0))));
    }

    @Test
    @Transactional
    public void tryToChangeAnotherUsersReservation() throws Exception{
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(16, 00));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(17, 00));
        ReservationDto dto = new ReservationDto(date, start, end);

        reservationService.makeReservation(dto, id);

        makeAuth(11);

        ReservationDto change = new ReservationDto(
            date,
            LocalDateTime.of(date, LocalTime.of(18, 0)),
            LocalDateTime.of(date, LocalTime.of(19, 0))
        );

        assertThrows(EntityNotFoundException.class,
            () -> reservationService.changeReservation(date, start, change, id));
    }
}
