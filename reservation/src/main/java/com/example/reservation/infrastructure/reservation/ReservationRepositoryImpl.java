package com.example.reservation.infrastructure.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.reservation.application.reservation.ReservationRepository;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.reservation.ReservationDomain;
import com.example.reservation.infrastructure.resource.Resource;
import com.example.reservation.infrastructure.resource.ResourceJpaRepository;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.infrastructure.user.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
  private final ReservationJpaRepository jpaRepository;
  private final ReservationAdapter adapter;
  private final UserJpaRepository userRepository;
  private final ResourceJpaRepository resourceRepository;

  @Override
  public ReservationDomain save(ReservationDomain domain) {
    Reservation toSave = adapter.toEntity(domain);
    Reservation saved = jpaRepository.save(toSave);

    return adapter.toDomain(saved);
  }

  @Override
  public List<ReservationDomain> findAll() {
    List<Reservation> list = jpaRepository.findAll();

    return list.stream()
      .map(adapter::toDomain)
      .toList();
  }

  @Override
  public Optional<ReservationDomain> findById(Long id) {
    return jpaRepository.findById(id)
      .map(adapter::toDomain);
  }

  @Override
  public List<ReservationDomain> findByDate(LocalDate date) {
    return jpaRepository.findByDate(date).stream()
      .map(adapter::toDomain)
      .toList();
  }  

  @Override
  public List<ReservationDomain> findByUser(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return jpaRepository.findByUser(user).stream()
      .map(adapter::toDomain)
      .toList();
  }

  @Override
  public Optional<ReservationDomain> findByUserAndStartTimeAndDate(Long userId, LocalTime startTime, LocalDate date) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return jpaRepository.findByUserAndStartTimeAndDate(user, startTime, date)
      .map(adapter::toDomain);
  }

  @Override
  public void deleteByUserAndStartTimeAndDate(Long userId, LocalTime startTime, LocalDate date) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    jpaRepository.deleteByUserAndStartTimeAndDate(user, startTime, date);
  }

  @Override
  public boolean existsOverlap(Long resourceId, LocalTime newStartTime,
    LocalTime newEndTime, LocalDate newDate) {
      Resource resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

      return jpaRepository.existsOverlap(resource, newStartTime, newEndTime, newDate);
    }

    @Override
  public List<ReservationDomain> findByDateAndResource(LocalDate date, Long resourceId) {
    Resource resource = resourceRepository.findById(resourceId)
      .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

    return jpaRepository.findByDateAndResource(date, resource).stream()
      .map(adapter::toDomain)
      .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    jpaRepository.deleteAll();
  }
}
