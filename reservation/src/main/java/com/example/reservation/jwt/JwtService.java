package com.example.reservation.jwt;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.user.User;
import com.example.reservation.user.UserRepository;

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
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.createToken(user);
        String newRefreshToken = refreshTokenService.UpdateRefreshToken(user, refreshToken);

        return Map.of("accessToken", newAccessToken, 
            "refreshToken", newRefreshToken);
    }
}
