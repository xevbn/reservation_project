package com.example.reservation;

import java.io.IOException;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.reservation.infrastructure.Security.CustomUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuthLoginProcessTest {
    public final MockMvc mvc;
    public static MockWebServer mockWebServer;

    public OAuthLoginProcessTest(MockMvc mvc) {
        this.mvc = mvc;
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
    public void testRedirectToProvider() throws Exception {
        mvc.perform(get("/oauth2/authorization/google"))
            .andExpect(status().isFound())
            .andExpect(header().string(HttpHeaders.LOCATION,
                org.hamcrest.Matchers.startsWith("https://accounts.google.com/o/oauth2/v2/auth")));
    }

    @Test
    public void testHandleCallbackAndLogin() throws Exception {
        String tokenResponse = "{\"access_token\": \"mock_access_token\", \"token_type\" \"Bearer\", \"expires_in\": 3600}";
        mockWebServer.enqueue(new MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(tokenResponse));

        String userInfoResponse = "{\"id\":\"12345\", \"name\":\"Test User\", \"email\":\"test@example.com\"}";
        mockWebServer.enqueue(new MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(userInfoResponse));

        mvc.perform(get("/oauth2/authorization/google")
            .queryParam("code", "mock_auth_code")
            .queryParam("state", "mock_state_value")
            .cookie(new Cookie("oauth2_auth_request", "mock_state_value"))
            )
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"))
            .andExpect(authenticated().withAuthenticationPrincipal(CustomUserDetails.class));
    }
}
