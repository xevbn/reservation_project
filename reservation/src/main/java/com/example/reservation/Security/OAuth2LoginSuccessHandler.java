package com.example.reservation.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.reservation.user.User;

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
    private final RefreshTokenRepository refreshTokenRepository;

    //oauth2 로그인 성공 시 jwt 토큰 발급
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
        HttpServletResponse res, Authentication auth) throws IOException, ServletException {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) auth.getPrincipal();

            User user = oAuth2User.getUser();

            String accessToken = jwtProvider.createToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);

            refreshTokenRepository.save(
                new RefreshToken(oAuth2User.getUserId(), oAuth2User.getName(), refreshToken)
            );

            res.setHeader("Authorization", "Bearer " + accessToken);

            Cookie cookie = new Cookie("refresh_token", accessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            res.addCookie(cookie);

            super.onAuthenticationSuccess(req, res, auth);
        }
}
