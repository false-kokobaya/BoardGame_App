package com.boardgameapp.config;

/**
 * 起動時に必須のシークレット（例: JWT secret）が未設定または空の場合にスローする。
 */
public class MissingSecretException extends IllegalStateException {

    public MissingSecretException(String message) {
        super(message);
    }
}
