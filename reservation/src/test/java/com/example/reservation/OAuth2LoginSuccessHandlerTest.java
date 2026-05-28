package com.example.reservation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import com.example.reservation.Security.CustomOAuth2User;
import com.example.reservation.Security.OAuth2LoginSuccessHandler;
import com.example.reservation.infrastructure.user.User;
import com.example.reservation.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class OAuth2LoginSuccessHandlerTest {
    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    OAuth2LoginSuccessHandler handler;

    @Test
    public void testSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication auth = mock(Authentication.class);

        CustomOAuth2User oAuth2User = mock(CustomOAuth2User.class);
        User user = new User("test", "passwd", "email");

        when(auth.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getUser()).thenReturn(user);
        when(jwtProvider.createToken(any())).thenReturn("access");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh");

        handler.onAuthenticationSuccess(request, response, auth);

        System.out.println(response.getHeader("Authorization").toString());

        assertEquals("Bearer access", response.getHeader("Authorization"));
        assertTrue(response.getHeader(HttpHeaders.SET_COOKIE).contains("refresh"));
    }
}
