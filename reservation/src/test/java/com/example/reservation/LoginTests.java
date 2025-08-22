package com.example.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {
    @Autowired
    MockMvc mvc;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void LoginWithNonExistingUser() throws Exception {
        mvc.perform(post("/login")
        .param("username", "username")
        .param("password", "password"))
        .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void registration() throws Exception {
        mvc.perform(post("/register")
        .param("username", "test_new_user")
        .param("password", "password")
        .param("email", "test1@email.com"))
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void LoginWithExistingUser() throws Exception {
        mvc.perform(post("/login")
        .param("username", "test_new_user")
        .param("password", "password"))
        .andExpect(redirectedUrl("/"));
    }
}
