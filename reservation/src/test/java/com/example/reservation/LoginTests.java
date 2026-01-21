package com.example.reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.reservation.jwt.RefreshTokenRepository;
import com.example.reservation.user.UserDto;
import com.example.reservation.user.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest()
@AutoConfigureMockMvc
@WithMockUser(username = "test")
public class LoginTests {
    @Autowired
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    UserService userService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void LoginWithNonExistingUser() throws Exception {
        UserDto dto = new UserDto("user", "passwd", "email");

        mvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void registration() throws Exception {
        UserDto dto = new UserDto("test_new_user", "password", "email");
        mvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(redirectedUrl("/login"))
            .andDo(print());
    }

    @Test
    public void LoginWithExistingUser() throws Exception {
        UserDto dto = new UserDto("test", "password", "email");
        userService.registration(dto);

        MvcResult res = mvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, String> responseBody = objectMapper.readValue(res.getResponse().getContentAsString(), 
            new TypeReference<Map<String, String>>() {});
        String accessToken = responseBody.get("Authorization");

        assertNotNull(accessToken);
    }

    @Test
    public void getUserDetailInfo() throws Exception {
        UserDto dto = new UserDto("test", "password", "email");
        userService.registration(dto);

        MvcResult res = mvc.perform(get("/user_detail"))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, String> resultBody = objectMapper.readValue(res.getResponse().getContentAsString(),
            new TypeReference<Map<String, String>>() {});
        String username = resultBody.get("username");

        assertEquals(username, "test");
    }

    @Test
    public void deleteUser() throws Exception {
        MvcResult rs = mvc.perform(delete("/user_detail/delete"))
            .andExpect(status().isNoContent())
            .andReturn();

        Map<String, String> body = objectMapper.readValue(rs.getResponse().getContentAsString(), 
            new TypeReference<Map<String, String>>() {});
        
        assertEquals(body.get("redirectUrl"), "/login");
    }

    @Test
    void whenAuthenticatedUser_thenCanAccessUserEndpoint() throws Exception {
        mvc.perform(get("/auth/me")
            .with(oauth2Login()
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .attributes(attrs -> {
                    attrs.put("email", "test@gmail.com");
                    attrs.put("name", "Test User");
                })))
            .andDo(print());
    }

    @Test
    void checkDuplicateEmail() throws Exception {
        UserDto dto = new UserDto("test", "password", "email");
        userService.registration(dto);
        
        UserDto dto2 = new UserDto("test2", "password", "email2");
        userService.registration(dto2);

        System.out.println(String.valueOf(userService.checkEmailDuplication("email")));

        MvcResult rs = mvc.perform(get("/check_email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("email", "asdf"))))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void logout() throws Exception{
        UserDto dto = new UserDto("test", "password", "email");
        userService.registration(dto);

        mvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        assertThat(!refreshTokenRepository.findAll().isEmpty());

        mvc.perform(get("/logout"))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
        
        assertThat(refreshTokenRepository.findAll().isEmpty());
    }
}
