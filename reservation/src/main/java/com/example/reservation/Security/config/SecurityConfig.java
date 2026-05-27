package com.example.reservation.Security.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.reservation.Security.jwt.JwtAuthorizationFilter;
import com.example.reservation.Security.jwt.JwtProvider;
import com.example.reservation.Security.oauth2.CustomOAuth2UserService;
import com.example.reservation.Security.oauth2.OAuth2LoginSuccessHandler;
import com.example.reservation.Security.oidc.CustomOidcUserService;
import com.example.reservation.Security.service.CustomUserDetailsService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    //formlogin에서 사용
    private final CustomUserDetailsService userDetailsService;
    private final SecretKey key;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    
    @Bean
    public SecurityFilterChain filter(HttpSecurity http, JwtProvider jwtProvider) throws Exception{
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(key, userDetailsService, jwtProvider);
        http
            .csrf((csrf) -> csrf.disable())
            .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((authentication) -> authentication
                .anyRequest().permitAll()/* 
                .requestMatchers("/auth/*", "/resource/list", "/check_email", "/register", "/login").permitAll()
                .requestMatchers("/user_detail", "/detail", "/{id}/detail").authenticated()
                .requestMatchers("/resource/*").hasRole("ADMIN")*/
            )
            //oauth 로그인 필터 추가 및 oidc 엔드포인트 추가
            .oauth2Login(oauth2 -> oauth2
                //oauth2 및 oidc 서비스 지정
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService))
                .successHandler(oAuth2LoginSuccessHandler))
            .addFilterBefore(jwtAuthorizationFilter, OAuth2LoginAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
