package com.boardgameapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.boardgameapp.dto.ErrorResponse;
import com.boardgameapp.exception.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * コントローラ全体の例外をハンドリングし、クライアントにエラー応答を返す。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String SAFE_ERROR_MESSAGE = "Invalid request parameters";

    /**
     * バリデーションエラー（@Valid）を 400 で返す。
     *
     * @param ex バリデーション例外
     * @return フィールド名とメッセージのマップ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * IllegalArgumentException（例: 未検出のリソース）を 400 で返す。
     * クライアントには汎用メッセージを返し、詳細はログに記録する。
     *
     * @param ex 例外
     * @return error キーに安全なメッセージ
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", SAFE_ERROR_MESSAGE);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * リソース未検出を 404 で返す。
     *
     * @param ex 例外
     * @return error メッセージ
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * IOException（例: 画像アップロード保存失敗）を 500 で返す。
     *
     * @param ex 例外
     * @return error メッセージ
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IOException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to save image. Please try again."));
    }

    /**
     * 認証失敗（ログイン不正）を 401 で返す。
     *
     * @param ex 認証例外
     * @return error メッセージ
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
