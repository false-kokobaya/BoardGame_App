package com.boardgameapp.controller;

import com.boardgameapp.dto.AuthResponse;
import com.boardgameapp.dto.LoginRequest;
import com.boardgameapp.dto.RegisterRequest;
import com.boardgameapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 認証API（登録・ログイン）を提供するコントローラ。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 新規ユーザーを登録する。
     *
     * @param request ユーザー名・メール・パスワード
     * @return トークンとユーザー情報
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ログインし、JWTトークンを返す。
     *
     * @param request ユーザー名・パスワード
     * @return トークンとユーザー情報
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
