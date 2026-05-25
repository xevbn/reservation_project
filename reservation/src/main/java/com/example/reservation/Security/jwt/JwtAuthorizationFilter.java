package com.example.reservation.Security.jwt;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

            String authHeader = request.getHeader("Authorization");
            if ( authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            //우선 토큰의 유효성 검증
            if (token != null && jwtProvider.validateToken(token)) {
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

                } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException | UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                    System.out.println(e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
        }
}
