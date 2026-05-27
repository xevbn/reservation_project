package com.example.reservation.Security.oauth2;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.reservation.Security.jwt.refreshToken.RefreshTokenService;
import com.example.reservation.Security.principal.CustomPrincipal;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.infrastructure.user.UserMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//stateless 구성으로 oauth2 로그인에 대한 jwt 발급을 위해 작성
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final RefreshTokenService refreshTokenService;

    //oauth2 로그인 성공 시 jwt 토큰 발급
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
        HttpServletResponse res, Authentication auth) throws IOException, ServletException {
            CustomPrincipal oAuth2User = (CustomPrincipal) auth.getPrincipal();

            User user = oAuth2User.getUser();

            String refreshToken = refreshTokenService.generateRefreshToken(UserMapper.toDomain(user));

            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            res.addCookie(cookie);

            res.sendRedirect("http://localhost:5173/oauth/success");

            super.onAuthenticationSuccess(req, res, auth);
        }
}
