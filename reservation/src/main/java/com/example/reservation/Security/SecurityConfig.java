package com.example.reservation.Security;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    //formlogin에서 사용
    private final CustomUserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;
    private final SecretKey key;
    
    @Bean
    public SecurityFilterChain filter(HttpSecurity http, JwtProvider jwtProvider) throws Exception{
        AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authManager, key, jwtProvider);
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(key, userDetailsService, jwtProvider);
        http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests((authentication) -> authentication
                .anyRequest().permitAll()
            )
            //oauth 로그인 필터 추가 및 oidc 엔드포인트 추가
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                //oauth2 및 oidc 서비스 지정
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService)))
            //oauth는 폼 로그인에서 사용 불가하므로 비활성화 후 페이지를 추가 등 필요
            .formLogin((form) -> form
                .permitAll())
            .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
            .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
