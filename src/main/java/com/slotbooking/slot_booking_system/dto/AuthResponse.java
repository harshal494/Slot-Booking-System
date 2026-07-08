package com.slotbooking.slot_booking_system.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private String token;

    private String username;
}
