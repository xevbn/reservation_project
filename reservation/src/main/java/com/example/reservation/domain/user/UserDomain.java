package com.example.reservation.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class UserDomain {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String userRole;
    private String provider;
    private String providerId;

    public UserDomain(Long id, String email, String username, String password, String userRole) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.provider = "local";
        this.providerId = "null";
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeUsername(String username) {
        this.username = username;
    }
}
