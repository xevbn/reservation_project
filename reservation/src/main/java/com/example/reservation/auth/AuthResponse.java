package com.example.reservation.auth;

import com.example.reservation.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class AuthResponse {
    private boolean valid;
    private User user;
}
