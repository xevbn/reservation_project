package com.example.reservation.auth;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation.jwt.JwtConfig;
import com.example.reservation.user.UserDto;

import lombok.AllArgsConstructor;



@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtConfig jwtConfig;

    //로그인 요청
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        LoginResponse res = authService.login(req);

        //리프레시 토큰을 httponly 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(jwtConfig.getRefreshExpiry() / 1000)
            .build();

        Long userId = res.getUser().getId();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("Authorization", res.getAccessToken(),
                        "userId", userId));
    }

    //토큰 만료 시
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue String refreshToken) {
        LoginResponse res = authService.refresh(refreshToken);

        //새로운 리프레시 토큰 httponly 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(jwtConfig.getRefreshExpiry() / 1000)
            .build();

        //응답에 httpOnly 쿠키 및 액세스 토큰 포함
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("Authorization", res.getAccessToken()));
    }
    
    
    //로그인되어있는지 확인하기 위해 요청하는 api 호출 res에 ok만 있으면 됨
    @GetMapping("/auth/me")
    public ResponseEntity<?> checkAuth(Authentication auth) {
        AuthResponse res = authService.CheckAuth(auth);
        
        if (res.isValid()) {
            UserDto dto = new UserDto(res.getUser());
            return ResponseEntity.ok(dto);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    //로그아웃 시 리프레시 토큰 삭제 등
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue String refreshToken) {
        authService.logout(refreshToken);

        ResponseCookie refresh = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Lax")
            .maxAge(0)
            .build();

        SecurityContextHolder.clearContext();

        ResponseEntity<?> res = ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refresh.toString())
            .body(Map.of("message", "successfully logged out"));

        return res;
    }
}
