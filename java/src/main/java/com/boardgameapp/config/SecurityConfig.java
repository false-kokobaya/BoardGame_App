package com.boardgameapp.config;

import com.boardgameapp.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security の設定（JWT認証・CORS・認可）。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of("http://localhost:5173", "http://localhost:3000");

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /** 認証・認可とCORSのフィルタチェーンを定義する。 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/uploads/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** フロントエンドオリジン向けのCORS設定を返す。 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = (allowedOriginsConfig == null || allowedOriginsConfig.isBlank())
                ? DEFAULT_ALLOWED_ORIGINS
                : Arrays.stream(allowedOriginsConfig.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins.isEmpty() ? DEFAULT_ALLOWED_ORIGINS : origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** パスワードハッシュ用のBCryptエンコーダを返す。 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
