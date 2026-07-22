package com.slotbooking.slot_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long showId;
    private String status;
    private List<String> seatLabels;
    private LocalDateTime createdAt;
}