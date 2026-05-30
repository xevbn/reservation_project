package com.example.reservation.application.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.reservation.domain.ReservationDomain;

public interface ReservationRepository {
  public boolean existsOverlap(long resourceId, LocalTime start, LocalTime end, LocalDate date);
  public List<ReservationDomain> findByDateAndResourceId(LocalDate date, long resourceId);
  public ReservationDomain save(ReservationDomain reservation);
  public List<ReservationDomain> findByDate(LocalDate date);
  public List<ReservationDomain> findByUserId(long userId);
  public Optional<ReservationDomain> findById(Long id);
  public void deleteById(Long id);
  public void deleteAll();
  public List<ReservationDomain> findAll();
}
