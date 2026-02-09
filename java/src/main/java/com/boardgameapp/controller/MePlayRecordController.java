package com.boardgameapp.controller;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.service.PlayRecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
     * 指定ゲームに紐づくプレイ記録一覧を取得する（ページング: page, size 指定可能）。
     *
     * @param auth 認証情報
     * @param userBoardGameId ユーザー所持ゲームID
     * @param pageable ページ・サイズ
     * @return プレイ記録のページ（プレイ日の降順）
     */
    @GetMapping("/boardgames/{userBoardGameId}/plays")
    public ResponseEntity<Page<PlayRecordResponse>> listByGame(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            Pageable pageable) {
        String username = auth.getName();
        Page<PlayRecordResponse> page = playRecordService.listByUserBoardGame(username, userBoardGameId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 認証ユーザーのプレイ記録を取得する（ページング: page, size 指定可能）。
     *
     * @param auth 認証情報
     * @param pageable ページ・サイズ
     * @return プレイ記録のページ
     */
    @GetMapping("/plays")
    public ResponseEntity<Page<PlayRecordResponse>> listAll(Authentication auth, Pageable pageable) {
        String username = auth.getName();
        Page<PlayRecordResponse> page = playRecordService.listAllByUsername(username, pageable);
        return ResponseEntity.ok(page);
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
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
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
        PlayRecordResponse updated = playRecordService.update(username, userBoardGameId, id, request);
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
        playRecordService.delete(username, userBoardGameId, id);
        return ResponseEntity.noContent().build();
    }
}
