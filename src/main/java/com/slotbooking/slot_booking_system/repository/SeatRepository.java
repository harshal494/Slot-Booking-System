package com.slotbooking.slot_booking_system.repository;

import com.slotbooking.slot_booking_system.entity.Seat;
import com.slotbooking.slot_booking_system.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShow(Show show);
}