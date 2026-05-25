package com.example.reservation.presentation.user.dto;

import com.example.reservation.domain.user.UserDomain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class AuthResponse {
    private boolean valid;
    private UserDomain user;
}
