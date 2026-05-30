package com.example.reservation.infrastructure.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.infrastructure.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getUserRole())
            .build();
    }

    //oauth2를 통해 로그인 시 해당 유저의 정보를 찾기 위한 메서드
    public UserDetails loadUserByProviderAndProviderID(String provider, String providerId) 
        throws UsernameNotFoundException {
            User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            
            return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
