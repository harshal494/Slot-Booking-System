package com.slotbooking.slot_booking_system.repository;

import com.slotbooking.slot_booking_system.entity.Seat;
import com.slotbooking.slot_booking_system.entity.Show;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShow(Show show);

    // plain fetch — used by optimistic path, @Version handles conflict detection on save
    List<Seat> findByIdIn(List<Long> ids);

    // pessimistic path — SELECT ... FOR UPDATE, real row lock held until transaction commits/rolls back
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id in :ids order by s.id asc")
    List<Seat> findByIdInForUpdate(List<Long> ids);
}