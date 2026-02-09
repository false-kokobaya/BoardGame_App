package com.boardgameapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ボードゲーム管理アプリケーションのエントリポイント。
 */
@SpringBootApplication
public class BoardGameApplication {

    /**
     * アプリケーションを起動する。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(BoardGameApplication.class, args);
    }
}
