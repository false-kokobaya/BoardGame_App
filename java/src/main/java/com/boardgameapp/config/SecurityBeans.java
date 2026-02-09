package com.boardgameapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * 認証まわりのBean定義（AuthenticationManager など）。
 */
@Configuration
public class SecurityBeans {

    /** ログイン認証に使う AuthenticationManager を提供する。 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
