package com.slotbooking.slot_booking_system.service;

import com.slotbooking.slot_booking_system.dto.AuthResponse;
import com.slotbooking.slot_booking_system.dto.LoginRequest;
import com.slotbooking.slot_booking_system.dto.RegisterRequest;
import com.slotbooking.slot_booking_system.entity.Role;
import com.slotbooking.slot_booking_system.entity.User;
import com.slotbooking.slot_booking_system.repository.UserRepository;
import com.slotbooking.slot_booking_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // hardcoded — never trust client-supplied role

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        return response;
    }
}