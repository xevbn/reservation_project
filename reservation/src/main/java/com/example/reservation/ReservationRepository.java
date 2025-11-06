package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface ReservationRepository extends CrudRepository<Reservation, Long>{
    public List<Reservation> findByDate(LocalDate date);
    public List<Reservation> findByUser(User user);
    public Optional<Reservation> findByUserAndStartTimeAndDate(User user, LocalTime startTime, LocalDate date);
    public void deleteByUserAndStartTimeAndDate(User user, LocalTime startTime, LocalDate date);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
        "FROM Reservation r " +
        "WHERE r.resource = :resource " +
        "AND r.date = :newDate " +
        "AND r.startTime < :newEndTime " +
        "AND :newStartTime < r.endTime")
    public boolean existsOverlap(@Param("resource") Resource resource,
                    @Param("newStartTime") LocalTime newStartTime,
                    @Param("newEndTime") LocalTime newEndTime,
                    @Param("newDate") LocalDate newDate);
    @Modifying
    @Transactional
    public void deleteByDateBefore(LocalDate date);
}
