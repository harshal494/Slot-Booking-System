package com.slotbooking.slot_booking_system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {

    @NotNull
    private Long showId;

    @NotEmpty
    @Size(max = 5, message = "Cannot book more than 5 seats per booking")
    private List<Long> seatIds;
}