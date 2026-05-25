package com.example.reservation.infrastructure.reservation;

import org.springframework.stereotype.Component;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.reservation.ReservationDomain;
import com.example.reservation.infrastructure.resource.ResourceJpaRepository;
import com.example.reservation.infrastructure.user.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationAdapter {
  private final UserJpaRepository userJpaRepository;
  private final ResourceJpaRepository resourceRepository;

  public ReservationDomain toDomain(Reservation entity) {
    return new ReservationDomain(entity.getId(),
      entity.getDate(),
      entity.getStartTime(),
      entity.getEndTime(),
      entity.getUser().getId(),
      entity.getResource().getId());
  }

  public Reservation toEntity(ReservationDomain domain) {
    return new Reservation(
      domain.getDate(),
      domain.getStartTime(),
      domain.getEndTime(),
      userJpaRepository.findById(domain.getUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)),
      resourceRepository.findById(domain.getResourceId())
        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
    );
  }
}
