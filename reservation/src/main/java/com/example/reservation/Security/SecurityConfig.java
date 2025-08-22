package com.example.reservation.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    //formlogin에서 사용
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception{
        http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests((authentication) -> authentication
                .anyRequest().permitAll()
            )
            .formLogin((form) -> form
                .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
