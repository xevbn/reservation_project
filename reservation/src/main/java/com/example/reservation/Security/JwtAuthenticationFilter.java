package com.example.reservation.Security;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//인증 시 jwt 발급하는 필터
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private final SecretKey key;
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(AuthenticationManager authManager, SecretKey key, JwtProvider jwtProvider) {
        super.setAuthenticationManager(authManager);
        this.key = key;
        this.jwtProvider = jwtProvider;
    }

    //인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse reponse)
        throws AuthenticationException {
            String username;
            String password;
            Map<String, String> creds;

            //request에서 credential을 얻어와 필요한 정보 추출
            try {
                creds = new ObjectMapper()
                    .readValue(request.getInputStream(), Map.class);
                username = creds.get("username");
                password = creds.get("password");
            
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //로그인 정보를 securityContextHolder에 추가
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authToken);
        }

    //인증 완료 시 헤더에 jwt 추가
    @Override
    public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
        FilterChain chain, Authentication authResult) throws IOException {
            String token = jwtProvider.createToken(authResult);
            res.addHeader("Authorization", "Bearer " + token);
        }
}
