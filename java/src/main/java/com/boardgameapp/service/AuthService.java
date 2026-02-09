package com.boardgameapp.service;

import com.boardgameapp.dto.AuthResponse;
import com.boardgameapp.dto.LoginRequest;
import com.boardgameapp.dto.RegisterRequest;
import com.boardgameapp.entity.User;
import com.boardgameapp.repository.UserRepository;
import com.boardgameapp.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

/**
 * ユーザー登録・ログインとJWT発行を行うサービス。
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 新規ユーザーを登録し、JWTトークンとユーザー情報を返す。
     * ユーザー名・メール重複時は 409 Conflict。一意制約違反（TOCTOU 含む）も同様に 409 で返す。
     *
     * @param request ユーザー名・メール・パスワード
     * @return トークンとユーザー名・ID
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user = userRepository.save(user);
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            return new AuthResponse(token, user.getUsername(), user.getId());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists", e);
        }
    }

    /**
     * ユーザー名・パスワードで認証し、JWTトークンとユーザー情報を返す。
     *
     * @param request ユーザー名・パスワード
     * @return トークンとユーザー名・ID
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jwtUtil.generateToken(username, user.getId());
        return new AuthResponse(token, username, user.getId());
    }
}
