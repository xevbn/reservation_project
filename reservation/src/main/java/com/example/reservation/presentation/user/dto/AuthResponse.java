package com.example.reservation.presentation.user.dto;

import com.example.reservation.infrastructure.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class AuthResponse {
    private boolean valid;
    private User user;
}
