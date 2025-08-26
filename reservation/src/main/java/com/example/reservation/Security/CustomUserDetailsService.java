package com.example.reservation.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.reservation.User;
import com.example.reservation.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자가 없습니다. " + username));

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
                .orElseThrow(() -> 
                    new UsernameNotFoundException("사용자가 없습니다. " + provider + "_" + providerId));
            
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getUserRole())
                .build();
    }
}
