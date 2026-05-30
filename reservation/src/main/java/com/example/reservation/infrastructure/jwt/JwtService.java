package com.example.reservation.infrastructure.jwt;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.infrastructure.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtService {
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    
    public Map<String, String> refresh(String refreshToken) {
        if(!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        
        Long userId = jwtProvider.getUserId(refreshToken);

        String newAccessToken = jwtProvider.createToken(userId);
        String newRefreshToken = refreshTokenService.UpdateRefreshToken(userId, refreshToken);

        return Map.of("accessToken", newAccessToken, 
            "refreshToken", newRefreshToken);
    }

    public String getUsername(String refreshToken) {
        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    
        return user.getUsername();
    }
}
