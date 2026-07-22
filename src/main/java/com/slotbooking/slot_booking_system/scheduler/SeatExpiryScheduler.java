package com.slotbooking.slot_booking_system.scheduler;

import com.slotbooking.slot_booking_system.entity.Booking;
import com.slotbooking.slot_booking_system.entity.BookingStatus;
import com.slotbooking.slot_booking_system.entity.Seat;
import com.slotbooking.slot_booking_system.entity.SeatStatus;
import com.slotbooking.slot_booking_system.repository.BookingRepository;
import com.slotbooking.slot_booking_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(SeatExpiryScheduler.class);

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Value("${booking.hold-expiry-minutes}")
    private int holdExpiryMinutes;

    private static final int PAGE_SIZE = 100;

    @Scheduled(fixedRate = 60000) // runs every 60 seconds
    @Transactional
    public void releaseExpiredHolds() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(holdExpiryMinutes);
        int releasedCount = 0;
        int pageNumber = 0;

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff, pageable);

        while (!page.isEmpty()) {
            List<Booking> expiredBookings = page.getContent();

            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
                for (Seat seat : booking.getSeats()) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat);
                }
                bookingRepository.save(booking);
                releasedCount++;
            }

            if (!page.hasNext()) break;
            pageable = pageable.next();
            page = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff, pageable);
        }

        log.info("Seat expiry scheduler run complete. Released {} expired bookings.", releasedCount);
    }
}