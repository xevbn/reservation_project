package com.example.reservation.presentation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private AuthInfo authInfo;

    public LoginResponse(String access, String refresh) {
        this.accessToken = access;
        this.refreshToken = refresh;
    }
}
