package com.boardgameapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("アプリケーションコンテキスト")
class BoardGameApplicationTest {

    @Test
    void コンテキストが正常に起動する() {
        // Spring Boot が起動し Bean がロードされれば成功
    }
}
