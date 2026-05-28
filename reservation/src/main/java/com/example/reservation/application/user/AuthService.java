package com.example.reservation.application.user;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.reservation.Security.CustomPrincipal;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.UserDomain;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.jwt.JwtProvider;
import com.example.reservation.jwt.JwtService;
import com.example.reservation.jwt.RefreshTokenService;
import com.example.reservation.presentation.user.dto.AuthInfo;
import com.example.reservation.presentation.user.dto.AuthResponse;
import com.example.reservation.presentation.user.dto.LoginResponse;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    public LoginResponse login(String username, String password) {
        //사용자 조회
        UserDomain user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        //securityContextHolder 세팅
        // Authentication auth = authManager.authenticate(
        //     new UsernamePasswordAuthenticationToken(username, password)
        // );

        //액세스 토큰 및 리프레시 토큰 발급
        String accessToken = jwtProvider.createToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        AuthInfo authInfo = getAuthInfo(user);
        LoginResponse res = new LoginResponse(accessToken, refreshToken, authInfo);

        log.info("Auth [로그인] - userId: {}", user.getId());

        return res;
    }

    public LoginResponse refresh(String refreshToken) {
        Map<String, String> tokens = jwtService.refresh(refreshToken);
        LoginResponse res = new LoginResponse(tokens.get("accessToken"), tokens.get("refreshToken"));

        return res;
    }

    //현재 인증 상황 및 사용자 정보 확인
    public AuthResponse CheckAuth(Authentication auth) {
        AuthResponse res = new AuthResponse();

        if (auth == null || !auth.isAuthenticated()) {
            res.setUser(null);
            res.setValid(false);

            return res;
        }

        CustomPrincipal principal = (CustomPrincipal) auth.getPrincipal();
        res.setUser(principal.getUser());
        res.setValid(true);

        return res;
    }

    @Transactional
    //로그 아웃 시 리프레시 토큰 삭제
    public void logout(String refreshToken) {
        Long userId = jwtProvider.getUserId(refreshToken);
        refreshTokenService.deleteByRefreshToken(refreshToken);
        log.info("Auth [로그아웃] - userId: {}", userId);
    }

    public AuthInfo getAuthInfo(UserDomain user) {
        return new AuthInfo(user.getId(), user.getUserRole());
    }
}
