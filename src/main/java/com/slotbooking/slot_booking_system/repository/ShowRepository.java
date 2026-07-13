package com.slotbooking.slot_booking_system.repository;

import com.slotbooking.slot_booking_system.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}