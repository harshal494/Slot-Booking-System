package com.slotbooking.slot_booking_system.repository;

import com.slotbooking.slot_booking_system.entity.Booking;
import com.slotbooking.slot_booking_system.entity.BookingStatus;
import com.slotbooking.slot_booking_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime cutoff, Pageable pageable);
//    List<Booking> findAll(User user);
    List<Booking> findByUser(User user);
}