package com.boardgameapp.controller;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.service.PlayRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 認証ユーザーのプレイ記録API（一覧・追加・更新・削除）を提供するコントローラ。
 */
@RestController
@RequestMapping("/api/me")
public class MePlayRecordController {

    private final PlayRecordService playRecordService;

    public MePlayRecordController(PlayRecordService playRecordService) {
        this.playRecordService = playRecordService;
    }

    /**
     * 指定ゲームに紐づくプレイ記録一覧を取得する。
     *
     * @param auth 認証情報
     * @param userBoardGameId ユーザー所持ゲームID
     * @return プレイ記録一覧（プレイ日の降順）
     */
    @GetMapping("/boardgames/{userBoardGameId}/plays")
    public ResponseEntity<List<PlayRecordResponse>> listByGame(
            Authentication auth,
            @PathVariable Long userBoardGameId) {
        String username = auth.getName();
        List<PlayRecordResponse> list = playRecordService.listByUserBoardGame(username, userBoardGameId);
        return ResponseEntity.ok(list);
    }

    /**
     * 認証ユーザーの全プレイ記録を取得する。
     *
     * @param auth 認証情報
     * @return プレイ記録一覧
     */
    @GetMapping("/plays")
    public ResponseEntity<List<PlayRecordResponse>> listAll(Authentication auth) {
        String username = auth.getName();
        List<PlayRecordResponse> list = playRecordService.listAllByUsername(username);
        return ResponseEntity.ok(list);
    }

    /**
     * 指定ゲームにプレイ記録を1件追加する。
     *
     * @param auth 認証情報
     * @param userBoardGameId ユーザー所持ゲームID
     * @param request プレイ日・メモ・人数
     * @return 作成されたプレイ記録
     */
    @PostMapping("/boardgames/{userBoardGameId}/plays")
    public ResponseEntity<PlayRecordResponse> add(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @Valid @RequestBody PlayRecordRequest request) {
        String username = auth.getName();
        PlayRecordResponse created = playRecordService.add(username, userBoardGameId, request);
        return ResponseEntity.ok(created);
    }

    /**
     * 指定IDのプレイ記録を更新する。
     *
     * @param auth 認証情報
     * @param userBoardGameId ユーザー所持ゲームID（URL用）
     * @param id プレイ記録ID
     * @param request 更新内容
     * @return 更新後のプレイ記録
     */
    @PutMapping("/boardgames/{userBoardGameId}/plays/{id}")
    public ResponseEntity<PlayRecordResponse> update(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @PathVariable Long id,
            @Valid @RequestBody PlayRecordRequest request) {
        String username = auth.getName();
        PlayRecordResponse updated = playRecordService.update(username, id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 指定IDのプレイ記録を削除する。
     *
     * @param auth 認証情報
     * @param userBoardGameId ユーザー所持ゲームID（URL用）
     * @param id プレイ記録ID
     * @return 204 No Content
     */
    @DeleteMapping("/boardgames/{userBoardGameId}/plays/{id}")
    public ResponseEntity<Void> delete(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @PathVariable Long id) {
        String username = auth.getName();
        playRecordService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
