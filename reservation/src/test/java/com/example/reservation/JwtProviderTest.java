package com.example.reservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.reservation.Security.JwtProvider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
//@WithMockUser(username="test", roles={"USER"})
public class JwtProviderTest {
    @Autowired
    JwtProvider jwtProvider;

    @Test
    public void createTokenTestWithUserDetails() {
        UserDetails userDetails = User.builder()
            .username("username")
            .password("password")
            .roles("USER")
            .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        
        String token = jwtProvider.createToken(authentication);

        assertNotNull(token);
    }

    @Test
    public void createTokenWithOAuth2User() throws Exception {
        Map<String, Object> attributes = Map.of(
            "sub", "1235",
            "email", "test@email.com",
            "name", "name"
        );

        OAuth2User oAuth2User = new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "email"
        );

        Authentication authentication = new OAuth2AuthenticationToken(
            oAuth2User,
            oAuth2User.getAuthorities(),
            "google"
        );

        String token = jwtProvider.createToken(authentication);
        assertNotNull(token);
    }

    @Test
    public void createTokenWithOidcUser() throws Exception {
        Map<String, Object> attributes = Map.of(
            "sub", "user",
            "email", "email",
            "name", "name"
        );

        String idToken = Jwts.builder()
            .setHeaderParam("typ", "jwt")
            .setIssuer("https://test-issuer.com")
            .setSubject("123456")
            .setAudience("test-client-id")
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .setIssuedAt(new Date())
            .addClaims(attributes)
            .signWith(SignatureAlgorithm.HS256, "testSecretKeytestSecretKeytestSecretKeytestSecretKey")
            .compact();

        OidcIdToken oidcIdToken = new OidcIdToken(idToken, Instant.now(),
            Instant.now().plus(1, ChronoUnit.HOURS), attributes);

        OidcUser oidcUser = new DefaultOidcUser(
            List.of(new SimpleGrantedAuthority("ROLE_USER")), 
            oidcIdToken);

        Authentication authentication = new OAuth2AuthenticationToken(
            oidcUser,
            oidcUser.getAuthorities(),
            "google"
        );

        String jwt = jwtProvider.createToken(authentication);
        assertNotNull(jwt, "토큰 생성");
        assertTrue(jwtProvider.validateToken(jwt), "유효성 검증");
    }
}
