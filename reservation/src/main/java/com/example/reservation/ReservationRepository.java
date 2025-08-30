package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ReservationRepository extends CrudRepository<Reservation, Long>{
    public List<Reservation> findByDate(LocalDate date);
    public List<Reservation> findByUser(User user);
    public Optional<Reservation> findByUserAndStartTimeAndDate(User user, LocalDateTime startTime, LocalDate date);
    public void deleteByUserAndStartTimeAndDate(User user, LocalDateTime startTime, LocalDate date);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
        "FROM Reservation r " +
        "WHERE r.resource = :resource " +
        "AND r.startTime < :newEndTime " +
        "AND :newStartTime < r.endTime")
boolean existsOverlap(@Param("resource") Resource resource,
                    @Param("newStartTime") LocalDateTime newStartTime,
                    @Param("newEndTime") LocalDateTime newEndTime);
}
