package com.slotbooking.slot_booking_system.controller;

import com.slotbooking.slot_booking_system.dto.SeatResponse;
import com.slotbooking.slot_booking_system.dto.ShowCreateRequest;
import com.slotbooking.slot_booking_system.dto.ShowResponse;
import com.slotbooking.slot_booking_system.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowCreateRequest request) {
        ShowResponse response = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getSeatsForShow(id));
    }
}