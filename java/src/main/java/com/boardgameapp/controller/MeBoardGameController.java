package com.boardgameapp.controller;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.service.UserBoardGameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 認証ユーザー所有のボードゲーム一覧・追加・更新・削除・1件取得APIを提供するコントローラ。
 */
@RestController
@RequestMapping("/api/me/boardgames")
public class MeBoardGameController {

    private final UserBoardGameService userBoardGameService;

    public MeBoardGameController(UserBoardGameService userBoardGameService) {
        this.userBoardGameService = userBoardGameService;
    }

    /**
     * 認証ユーザーのボードゲーム一覧を取得する。
     *
     * @param auth 認証情報
     * @return 追加日時の降順のボードゲーム一覧
     */
    @GetMapping
    public ResponseEntity<List<UserBoardGameResponse>> list(Authentication auth) {
        String username = auth.getName();
        List<UserBoardGameResponse> list = userBoardGameService.listByUsername(username);
        return ResponseEntity.ok(list);
    }

    /**
     * 認証ユーザーにボードゲームを1件追加する。
     *
     * @param auth 認証情報
     * @param request ゲーム名・サムネURL・年など
     * @return 作成されたゲーム情報
     */
    @PostMapping
    public ResponseEntity<UserBoardGameResponse> add(
            Authentication auth,
            @Valid @RequestBody AddBoardGameRequest request) {
        String username = auth.getName();
        UserBoardGameResponse created = userBoardGameService.add(username, request);
        return ResponseEntity.ok(created);
    }

    /**
     * 指定IDのボードゲームを更新する。
     *
     * @param auth 認証情報
     * @param id ゲームID
     * @param request 更新内容
     * @return 更新後のゲーム情報
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserBoardGameResponse> update(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardGameRequest request) {
        String username = auth.getName();
        UserBoardGameResponse updated = userBoardGameService.update(username, id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 指定IDのボードゲームを削除する（紐づくプレイ記録も削除される）。
     *
     * @param auth 認証情報
     * @param id ゲームID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        userBoardGameService.delete(username, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 指定IDのボードゲームを1件取得する。
     *
     * @param auth 認証情報
     * @param id ゲームID
     * @return ゲーム情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserBoardGameResponse> get(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        UserBoardGameResponse response = userBoardGameService.getByIdAndUsername(id, username);
        return ResponseEntity.ok(response);
    }
}
