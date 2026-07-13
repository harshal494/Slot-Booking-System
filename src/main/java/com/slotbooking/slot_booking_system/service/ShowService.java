package com.slotbooking.slot_booking_system.service;

import com.slotbooking.slot_booking_system.dto.SeatResponse;
import com.slotbooking.slot_booking_system.dto.ShowCreateRequest;
import com.slotbooking.slot_booking_system.dto.ShowResponse;
import com.slotbooking.slot_booking_system.entity.Seat;
import com.slotbooking.slot_booking_system.entity.SeatStatus;
import com.slotbooking.slot_booking_system.entity.Show;
import com.slotbooking.slot_booking_system.repository.SeatRepository;
import com.slotbooking.slot_booking_system.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public ShowResponse createShow(ShowCreateRequest request) {
        Show show = new Show();
        show.setName(request.getName());
        show.setTotalSeats(request.getTotalSeats());
        showRepository.save(show);

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= request.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setShow(show);
            seat.setLabel(String.valueOf(i));
            seat.setStatus(SeatStatus.AVAILABLE);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);

        return new ShowResponse(show.getId(), show.getName(), show.getTotalSeats());
    }

    public List<ShowResponse> getAllShows() {
        return showRepository.findAll().stream()
                .map(s -> new ShowResponse(s.getId(), s.getName(), s.getTotalSeats()))
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getSeatsForShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found"));

        return seatRepository.findByShow(show).stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getLabel(), seat.getStatus().name()))
                .collect(Collectors.toList());
    }
}