package com.boardgameapp.controller;

import com.boardgameapp.dto.AuthResponse;
import com.boardgameapp.dto.LoginRequest;
import com.boardgameapp.dto.RegisterRequest;
import com.boardgameapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {
        @Test
        void 有効なリクエストで登録しトークンとユーザー情報を返す() throws Exception {
            RegisterRequest body = new RegisterRequest();
            body.setUsername("newuser");
            body.setEmail("new@example.com");
            body.setPassword("password6");

            AuthResponse authResp = new AuthResponse("jwt-token", "newuser", 1L);
            when(authService.register(any(RegisterRequest.class))).thenReturn(authResp);

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.username").value("newuser"))
                    .andExpect(jsonPath("$.userId").value(1));

            verify(authService).register(any(RegisterRequest.class));
        }

        @Test
        void usernameが空なら400() throws Exception {
            RegisterRequest body = new RegisterRequest();
            body.setUsername("");
            body.setEmail("a@b.com");
            body.setPassword("password6");

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {
        @Test
        void 有効なリクエストでログインしトークンを返す() throws Exception {
            LoginRequest body = new LoginRequest();
            body.setUsername("user");
            body.setPassword("pass1234");

            AuthResponse authResp = new AuthResponse("token-xyz", "user", 2L);
            when(authService.login(any(LoginRequest.class))).thenReturn(authResp);

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token-xyz"))
                    .andExpect(jsonPath("$.username").value("user"));

            verify(authService).login(any(LoginRequest.class));
        }
    }
}
