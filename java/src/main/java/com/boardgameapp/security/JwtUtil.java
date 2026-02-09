package com.boardgameapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT トークンの生成・検証・クレーム取得を行うユーティリティ。
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** ユーザー名とIDをクレームに含むJWTを発行する。 */
    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** トークンからユーザー名（subject）を取得する。 */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** トークンからユーザーIDクレームを取得する。 */
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /** トークンが有効かつ指定ユーザー名と一致するか検証する。 */
    public boolean validateToken(String token, String username) {
        try {
            String subject = extractUsername(token);
            return subject.equals(username) && !isExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
