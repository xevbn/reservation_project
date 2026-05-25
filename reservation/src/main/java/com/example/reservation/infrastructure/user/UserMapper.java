package com.example.reservation.infrastructure.user;

import com.example.reservation.domain.user.UserDomain;

public class UserMapper {
    public static User toEntity(UserDomain domain) {
        return new User(
            domain.getId(), 
            domain.getEmail(),
            domain.getPassword(),
            domain.getUsername(),
            domain.getProvider(),
            domain.getProviderId(),
            domain.getUserRole()
        );
    }

    public static UserDomain toDomain(User user) {
        return UserDomain.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .provider(user.getProvider())
            .providerId(user.getProviderId())
            .userRole(user.getUserRole())
            .build();
    }
}
