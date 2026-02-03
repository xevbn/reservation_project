package com.example.reservation.jwt;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.reservation.Security.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
            String token = null;
            Cookie cookies[] = request.getCookies();

            String authHeader = request.getHeader("Authorization");
            if ( authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                System.out.println("토큰이 없거나 형식이 틀림: " + authHeader);
            }

            //우선 토큰의 유효성 검증
            if (token != null) {
                //jwt에서 필요한 정보를 추출
                try {
                    Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                    UserDetails userDetails = customUserDetailsService.loadUserById(Long.valueOf(claims.getSubject()));

                    //해당 유저 정보가 확인된다면
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch(ExpiredJwtException e) {
                    System.out.println("jwt 만료로 재발급 응답 송신");

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;UTF-8");
                    response.getWriter().write("{\"code\":\"TOKEN_EXPIRED\", \"message\":\"토큰 만료됨\"}");

                    return;
                } catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException | UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                    System.out.println(e.getMessage());
                } 


            System.out.println("예외 안 걸림 만료 신호 없음");
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        String path = req.getRequestURI();
        List<String> exclude = List.of("/login", "/register", "/auth/refresh", "/oauth2/authorize/*", "/reservation/sse/*");

        return exclude.stream().anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }
}
