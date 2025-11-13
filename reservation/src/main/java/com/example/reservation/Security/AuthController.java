package com.example.reservation.Security;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.user.User;
import com.example.reservation.user.UserDto;
import com.example.reservation.user.UserService;

import lombok.AllArgsConstructor;



@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    //로그인 요청
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        
        //securityContextHolder 세팅
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userService.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //액세스 토큰 및 리프레시 토큰 발급
        String accessToken = jwtProvider.createToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        //리프레시 토큰 저장
        RefreshToken saveRefreshToken = new RefreshToken(user.getId(), username, refreshToken);
        refreshTokenRepository.save(saveRefreshToken);

        //리프레시 토큰을 httponly 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(7 * 60 * 60 * 24)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.COOKIE, cookie.toString())
            .body(Map.of("Authorization", "Bearer " +accessToken));
    }

    //토큰 만료 시
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue String refreshToken) {
        //리프레시 토큰 유효하지 않을 시 에러 반환
        if(!jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Token"));
        }

        //리프레시 토큰에서 username 가져와 DB와 비교
        Long userId = jwtProvider.getUserId(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("No refresh token"));

        User user = userService.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //리프레시 토큰이 동일하지 않을 시 에러 반환
        if(!stored.getRefreshToken().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token mismatch"));
        }

        //새로운 토큰 발급
        String newAccessToken = jwtProvider.createToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        stored.setRefreshToken(newRefreshToken);
        refreshTokenRepository.save(stored);

        //새로운 리프레시 토큰 httponly 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("accessToken", newAccessToken));
    }
    
    
    //로그인되어있는지 확인하기 위해 요청하는 api 호출 res에 ok만 있으면 됨
    @GetMapping("/auth/me")
    public ResponseEntity<?> checkAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDto userDto = new UserDto();

        //정확한 확인을 위해 바디에 유저 정보 추가
        if (auth.getPrincipal() instanceof CustomOAuth2User oAuth2User) {
            userDto.setUsername(oAuth2User.getName());
            userDto.setEmail(oAuth2User.getEmail());
        } else if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            userDto.setUsername(userDetails.getUsername());
            userDto.setEmail(userDetails.getEmail());
        }

        return ResponseEntity.ok(userDto);
    }
    
}
