package com.example.reservation.application.resrvation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.reservation.domain.ReservationDomain;

public interface ReservationRepository {
  public ReservationDomain save(ReservationDomain reservation);
  public boolean existsOverlap(Long resourceId, LocalTime start, LocalTime end, LocalDate date);
  public Optional<ReservationDomain> findById(Long id);
  public void deleteById(Long id);
  public List<ReservationDomain> findByResourceIdAndDate(Long resourceId, LocalDate date);
  public Optional<ReservationDomain> findByUserIdAndDate(Long userId, LocalDate date);
  public List<ReservationDomain> findByDate(LocalDate date);
  public List<ReservationDomain> findByUser(Long userId);
  public List<ReservationDomain> findAll();
  public void deleteAll();
  public void deleteByDateBefore(LocalDate date);
}
