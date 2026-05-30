package com.example.reservation.infrastructure.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.reservation.infrastructure.jwt.JwtConfig;
import com.example.reservation.infrastructure.jwt.JwtProvider;
import com.example.reservation.infrastructure.jwt.RefreshTokenService;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.presentation.user.dto.AuthInfo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//stateless 구성으로 oauth2 로그인에 대한 jwt 발급을 위해 작성
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    //oauth2 로그인 성공 시 jwt 토큰 발급
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
        HttpServletResponse res, Authentication auth) throws IOException, ServletException {
            CustomPrincipal oAuth2User = (CustomPrincipal) auth.getPrincipal();

            User user = oAuth2User.getUser();

            String accessToken = jwtProvider.createToken(user.getId());
            String refreshToken = refreshTokenService.generateRefreshToken(user.getId());

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(JwtConfig.getRefreshExpirySec());
            res.addCookie(cookie);

            AuthInfo userInfo = new AuthInfo(user.getId(), user.getUserRole());

            res.sendRedirect("http://localhost:5173/oauth/success?accessToken=" + accessToken + "&userInfo=" + userInfo.toString());

            super.onAuthenticationSuccess(req, res, auth);
        }
}
