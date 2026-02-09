package com.boardgameapp.controller;

import com.boardgameapp.dto.AddWishlistItemRequest;
import com.boardgameapp.dto.WishlistItemResponse;
import com.boardgameapp.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * 認証ユーザーのほしいものリストAPIを提供するコントローラ。
 */
@RestController
@RequestMapping("/api/me/wishlist")
public class MeWishlistController {

    private final WishlistService wishlistService;

    public MeWishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * 認証ユーザーのほしいものリスト一覧を取得する。
     *
     * @param auth 認証情報
     * @return ほしいものリスト一覧
     */
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> list(Authentication auth) {
        String username = auth.getName();
        List<WishlistItemResponse> list = wishlistService.listByUsername(username);
        return ResponseEntity.ok(list);
    }

    /**
     * ほしいものリストに1件追加する。
     *
     * @param auth 認証情報
     * @param request ゲーム名・BGG IDなど
     * @return 追加されたアイテム
     */
    @PostMapping
    public ResponseEntity<WishlistItemResponse> add(
            Authentication auth,
            @Valid @RequestBody AddWishlistItemRequest request) {
        String username = auth.getName();
        WishlistItemResponse created = wishlistService.add(username, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * ほしいものリストから指定IDのアイテムを削除する。
     *
     * @param auth 認証情報
     * @param id ほしいものアイテムID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        wishlistService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
