package com.boardgameapp.service;

import com.boardgameapp.dto.AuthResponse;
import com.boardgameapp.dto.LoginRequest;
import com.boardgameapp.dto.RegisterRequest;
import com.boardgameapp.entity.User;
import com.boardgameapp.repository.UserRepository;
import com.boardgameapp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService sut;

    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final Long USER_ID = 1L;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setUsername(USERNAME);
        savedUser.setEmail(EMAIL);
        savedUser.setPasswordHash("encodedHash");
    }

    @Nested
    @DisplayName("register")
    class Register {
        @Test
        void 新規ユーザーを登録しトークン付きレスポンスを返す() {
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedHash");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(USER_ID);
                return u;
            });
            when(jwtUtil.generateToken(USERNAME, USER_ID)).thenReturn("jwt-token");

            RegisterRequest request = new RegisterRequest();
            request.setUsername(USERNAME);
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            AuthResponse result = sut.register(request);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
            assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
            assertThat(captor.getValue().getPasswordHash()).isEqualTo("encodedHash");

            assertThat(result.getToken()).isEqualTo("jwt-token");
            assertThat(result.getUsername()).isEqualTo(USERNAME);
            assertThat(result.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        void ユーザー名が重複していれば409Conflict() {
            when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

            RegisterRequest request = new RegisterRequest();
            request.setUsername(USERNAME);
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            assertThatThrownBy(() -> sut.register(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT)
                    .hasMessageContaining("Username already exists");
        }

        @Test
        void メールが重複していれば409Conflict() {
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            RegisterRequest request = new RegisterRequest();
            request.setUsername(USERNAME);
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            assertThatThrownBy(() -> sut.register(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT)
                    .hasMessageContaining("Email already exists");
        }
    }

    @Nested
    @DisplayName("login")
    class Login {
        @Test
        void 認証に成功すればトークン付きレスポンスを返す() {
            Authentication auth = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(savedUser));
            when(jwtUtil.generateToken(USERNAME, USER_ID)).thenReturn("jwt-token");

            LoginRequest request = new LoginRequest();
            request.setUsername(USERNAME);
            request.setPassword(PASSWORD);

            AuthResponse result = sut.login(request);

            assertThat(result.getToken()).isEqualTo("jwt-token");
            assertThat(result.getUsername()).isEqualTo(USERNAME);
            assertThat(result.getUserId()).isEqualTo(USER_ID);
        }
    }
}
