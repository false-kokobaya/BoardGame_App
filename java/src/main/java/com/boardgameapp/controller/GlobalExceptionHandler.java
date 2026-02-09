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

import jakarta.servlet.http.HttpServletRequest;
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
     * @return ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(SAFE_ERROR_MESSAGE));
    }

    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";

    /**
     * リソース未検出を 404 で返す。クライアントには汎用メッセージを返し、詳細はログに記録する。
     *
     * @param ex 例外
     * @return ErrorResponse（サニタイズ済みメッセージ）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(RESOURCE_NOT_FOUND_MESSAGE));
    }

    /**
     * IOException（例: 画像アップロード保存失敗）を 500 で返す。
     *
     * @param ex 例外
     * @return ErrorResponse
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IOException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to save image. Please try again."));
    }

    /**
     * 認証失敗（ログイン不正）を 401 で返す。リクエスト情報を WARN でログ（パスワードは含めない）。
     *
     * @param ex 認証例外
     * @param req リクエスト（ログ用）
     * @return ErrorResponse
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        String username = req.getParameter("username");
        log.warn("Authentication failed: remoteAddr={}, uri={}, username={}",
                req.getRemoteAddr(), req.getRequestURI(), username != null ? username : "(none)", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid username or password"));
    }
}
