package com.example.reservation.Security;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;


//로그인 후 인가를 확인하기 위한 필터
//로그인 후 클라이언트에서 요청을 보낼 때마다 이 필터를 거침
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final SecretKey key;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtProvider jwtProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
            String token = null;

            //jwt가 쿠키에 포함되어 있는지 헤더에 포함되어 있는지 확인하여 String 형태로 추출
            if ( request.getCookies() != null ) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwtToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            } else {
                String authHeader = request.getHeader("Authorization");
                if ( authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            //jwt가 없으면 필터를 넘김
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            //우선 토큰의 유효성 검증
            if (jwtProvider.validateToken(token)) {
                //jwt에서 필요한 정보를 추출
                try {
                    Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                    String[] parts = claims.getSubject().split("_");
                    UserDetails userDetails;

                    //일반 로그인 시와 oauth2를 통한 로그인에 대한 분기점
                    if (parts.length > 1) {
                        userDetails = customUserDetailsService.loadUserByProviderAndProviderID(parts[0], parts[1]);
                    } else {
                        userDetails = customUserDetailsService.loadUserByUsername(parts[0]);
                    }

                    //해당 유저 정보가 확인된다면
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }

                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
}
