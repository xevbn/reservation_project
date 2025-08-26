package com.example.reservation.Security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey key;
    private final JwtConfig jwtConfig;

    public String createToken(Authentication authentication) {
        String sub;
        List<String> authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        Object principal = authentication.getPrincipal();
        String provider;

        //로그인 방법으로 분기 
        if ( principal instanceof UserDetails userDetails) {
            sub = userDetails.getUsername();
            provider = "local";
        } else if ( authentication instanceof OAuth2AuthenticationToken oauth2Token ) {
            provider = oauth2Token.getAuthorizedClientRegistrationId();

            if ( principal instanceof OidcUser oidcUser ) {
                sub = oidcUser.getSubject();
            } else if ( principal instanceof OAuth2User oAuth2User ) {
                sub = oAuth2User.getAttribute("id");
            } else {
                throw new IllegalArgumentException("unknown principal");
            }
        } else {
            throw new IllegalArgumentException("unknown principal");
        }        

        //jwt에 넣을 정보(필요 시 추가 요망)
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub + "_" + provider);
        claims.put("role", authorities);

        //jwt 서명해서 반환
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiry()))
            .signWith(key)
            .compact();
    }

    //jwt가 유효한지 판단
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
