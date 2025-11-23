package com.example.reservation.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.reservation.Security.CustomPrincipal;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.jwt.JwtProvider;
import com.example.reservation.jwt.RefreshToken;
import com.example.reservation.jwt.RefreshTokenService;
import com.example.reservation.user.User;
import com.example.reservation.user.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        //사용자 조회
        User user = userService.findByUsername(req.getUsername())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        //securityContextHolder 세팅
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        //액세스 토큰 및 리프레시 토큰 발급
        String accessToken = jwtProvider.createToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        LoginResponse res = new LoginResponse(accessToken, refreshToken, user);

        return res;
    }

    public LoginResponse refresh(String refreshToken) {
        //리프레시 토큰 유효하지 않을 시 에러 반환
        if(!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        //리프레시 토큰에서 userId 가져와 DB와 비교
        Long userId = jwtProvider.getUserId(refreshToken);
        RefreshToken stored = refreshTokenService.getRefreshTokenByUserId(userId);

        User user = userService.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //리프레시 토큰이 동일하지 않을 시 에러 반환
        if(!stored.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        //새로운 토큰 발급
        String newAccessToken = jwtProvider.createToken(user);
        String newRefreshToken = refreshTokenService.UpdateRefreshToken(user);

        LoginResponse res = new LoginResponse(newAccessToken, newRefreshToken, user);

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
}
