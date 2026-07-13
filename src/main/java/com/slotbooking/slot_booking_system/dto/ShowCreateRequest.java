package com.slotbooking.slot_booking_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowCreateRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int totalSeats;
}