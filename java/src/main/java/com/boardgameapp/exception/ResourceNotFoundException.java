package com.boardgameapp.exception;

/**
 * 要求されたリソース（ユーザー・ボードゲーム・プレイ記録など）が存在しない場合にスローする。
 * グローバルハンドラで HTTP 404 にマッピングする。
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
