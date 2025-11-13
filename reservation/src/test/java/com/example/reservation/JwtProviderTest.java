package com.example.reservation;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.HeaderAssertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.reservation.Security.JwtProvider;
import com.example.reservation.user.UserDto;
import com.example.reservation.user.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Slf4j
@SpringBootTest()
@AutoConfigureMockMvc
@Transactional
//@WithMockUser(username="test", roles={"USER"})
public class JwtProviderTest {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    MockMvc mvc;
    @Autowired
    UserService userService;
    ObjectMapper objectMapper;
    static MockWebServer mockWebServer;

    @BeforeEach
    void setUp() {
        UserDto dto = new UserDto("user", "passwd", "email");
        userService.registration(dto);
        objectMapper = new ObjectMapper();
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        String baseUrl = "http://localhost:" + mockWebServer.getPort();
        registry.add("spring.security.oauth2.client.provider.google.token-uri", () -> baseUrl + "/token");
        registry.add("spring.security.oauth2.client.provider.google.user-info-uri", () -> baseUrl + "/userinfo");
    }

    @Test
    public void loginThenGetToken() throws Exception{
        UserDto dto = new UserDto();
        dto.setUsername("user");
        dto.setPassword("passwd");

        MvcResult result = mvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, String> responseBody = objectMapper.readValue(result.getResponse().getContentAsString()
            , new TypeReference<Map<String, String>>() {});

        String accessToken = responseBody.get("Authorization");

        System.out.println(result);

        assertNotNull(accessToken);
    }
}
