package com.boardgameapp.controller;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.service.UserBoardGameService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    private static String requireUsername(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return auth.getName();
    }

    /**
     * 認証ユーザーのボードゲーム一覧を取得する（ページング: page, size をクエリパラメータで指定可能）。
     *
     * @param auth 認証情報
     * @param pageable ページ・サイズ（ソートはサービス側で追加日降順に固定）
     * @return 追加日時の降順のボードゲーム一覧
     */
    @GetMapping
    public ResponseEntity<List<UserBoardGameResponse>> list(Authentication auth, Pageable pageable) {
        String username = requireUsername(auth);
        List<UserBoardGameResponse> content = userBoardGameService.listByUsername(username, pageable).getContent();
        return ResponseEntity.ok(content);
    }

    /**
     * 認証ユーザーにボードゲームを1件追加する。
     *
     * @param auth 認証情報
     * @param request ゲーム名・サムネURL・年など
     * @return 作成されたゲーム情報（201 Created + Location）
     */
    @PostMapping
    public ResponseEntity<UserBoardGameResponse> add(
            Authentication auth,
            @Valid @RequestBody AddBoardGameRequest request) {
        String username = requireUsername(auth);
        UserBoardGameResponse created = userBoardGameService.add(username, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
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
        String username = requireUsername(auth);
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
        String username = requireUsername(auth);
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
        String username = requireUsername(auth);
        UserBoardGameResponse response = userBoardGameService.getByIdAndUsername(id, username);
        return ResponseEntity.ok(response);
    }
}
