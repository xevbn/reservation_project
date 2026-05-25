package com.example.reservation.application.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.reservation.domain.reservation.ReservationDomain;

@Repository
public interface ReservationRepository {
	public ReservationDomain save(ReservationDomain domain);
	public List<ReservationDomain> findAll();
	public Optional<ReservationDomain> findById(Long id);
  public List<ReservationDomain> findByDate(LocalDate date);
	public List<ReservationDomain> findByUser(Long userId);
	public Optional<ReservationDomain> findByUserAndStartTimeAndDate(Long userId, LocalTime startTime, LocalDate date);
	public void deleteByUserAndStartTimeAndDate(Long userId, LocalTime startTime, LocalDate date);
	public boolean existsOverlap(Long resourceId, LocalTime newStartTime,
    LocalTime newEndTime, LocalDate newDate);
	public List<ReservationDomain> findByDateAndResource(LocalDate date, Long resourceId);
	public void deleteById(Long id);
	public void deleteAll();
}
