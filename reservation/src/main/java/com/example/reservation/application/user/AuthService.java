package com.example.reservation.application.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.reservation.Security.jwt.JwtProvider;
import com.example.reservation.Security.jwt.RefreshToken;
import com.example.reservation.Security.jwt.RefreshTokenService;
import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.user.UserDomain;
import com.example.reservation.presentation.user.dto.AuthResponse;
import com.example.reservation.presentation.user.dto.LoginRequest;
import com.example.reservation.presentation.user.dto.LoginResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final CurrentUserport currentUserport;

    public LoginResponse login(LoginRequest req) {
        //사용자 조회
        UserDomain user = userService.findByUsername(req.getUsername());

        //액세스 토큰 및 리프레시 토큰 발급
        String accessToken = jwtProvider.createToken(user.getId());
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

        UserDomain user = userService.findById(userId);

        //리프레시 토큰이 동일하지 않을 시 에러 반환
        if(!stored.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        //새로운 토큰 발급
        String newAccessToken = jwtProvider.createToken(user.getId());
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

        Long userId = currentUserport.getUserId();
        UserDomain curUser = userService.findById(userId);

        res.setUser(curUser);
        res.setValid(true);

        return res;
    }

    //로그 아웃 시 리프레시 토큰 삭제
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }

    //액세스 토큰 발급
    public String getAccessToken(UserDomain user) {
        return jwtProvider.createToken(user.getId());
    }
}
