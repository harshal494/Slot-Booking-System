package com.slotbooking.slot_booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
