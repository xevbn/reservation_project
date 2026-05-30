package com.example.reservation.application.resrvation;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationCleanupService {
    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteExpiredReservations() {
        LocalDate today = LocalDate.now();
        reservationRepository.deleteByDateBefore(today);
    }
}
