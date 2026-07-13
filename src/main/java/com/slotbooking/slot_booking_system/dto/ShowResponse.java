package com.slotbooking.slot_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShowResponse {
    private Long id;
    private String name;
    private int totalSeats;
}
