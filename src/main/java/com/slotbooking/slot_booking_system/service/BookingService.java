package com.slotbooking.slot_booking_system.service;

import com.slotbooking.slot_booking_system.dto.BookingRequest;
import com.slotbooking.slot_booking_system.dto.BookingResponse;
import com.slotbooking.slot_booking_system.entity.*;
import com.slotbooking.slot_booking_system.repository.BookingRepository;
import com.slotbooking.slot_booking_system.repository.SeatRepository;
import com.slotbooking.slot_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));
    }

    private List<Long> sorted(List<Long> ids) {
        return ids.stream().sorted().collect(Collectors.toList());
    }

    private BookingResponse toResponse(Booking booking) {
        Long showId = booking.getSeats().isEmpty() ? null : booking.getSeats().get(0).getShow().getId();
        List<String> labels = booking.getSeats().stream().map(Seat::getLabel).collect(Collectors.toList());
        return new BookingResponse(booking.getId(), showId, booking.getStatus().name(), labels, booking.getCreatedAt());
    }

    @Transactional
    public BookingResponse bookOptimistic(BookingRequest request) {
        List<Long> seatIds = sorted(request.getSeatIds());
        List<Seat> seats = seatRepository.findByIdIn(seatIds);

        validateSeats(seats, seatIds, request.getShowId());

        try {
            for (Seat seat : seats) {
                seat.setStatus(SeatStatus.PENDING);
                seatRepository.save(seat);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new IllegalStateException("Seat(s) already booked by another request. Please try again.");
        }

        return persistBooking(seats);
    }

    @Transactional
    public BookingResponse bookPessimistic(BookingRequest request) {
        List<Long> seatIds = sorted(request.getSeatIds());
        List<Seat> seats = seatRepository.findByIdInForUpdate(seatIds);

        validateSeats(seats, seatIds, request.getShowId());

        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.PENDING);
            seatRepository.save(seat);
        }

        return persistBooking(seats);
    }

    private void validateSeats(List<Seat> seats, List<Long> requestedIds, Long showId) {
        if (seats.size() != requestedIds.size()) {
            throw new IllegalArgumentException("One or more requested seats do not exist");
        }

        List<Long> unavailable = seats.stream()
                .filter(s -> s.getStatus() != SeatStatus.AVAILABLE)
                .map(Seat::getId)
                .collect(Collectors.toList());

        if (!unavailable.isEmpty()) {
            throw new IllegalArgumentException("Seat(s) " + unavailable + " already booked");
        }

        boolean wrongShow = seats.stream().anyMatch(s -> !s.getShow().getId().equals(showId));
        if (wrongShow) {
            throw new IllegalArgumentException("One or more seats do not belong to the specified show");
        }
    }

    private BookingResponse persistBooking(List<Seat> seats) {
        Booking booking = new Booking();
        booking.setUser(currentUser());
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        User user = currentUser();
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You are not authorized to confirm this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        User user = currentUser();
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You are not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }
        bookingRepository.save(booking);
    }

    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        User user = currentUser();
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You are not authorized to view this booking");
        }

        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {
        User user = currentUser();
        return bookingRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}