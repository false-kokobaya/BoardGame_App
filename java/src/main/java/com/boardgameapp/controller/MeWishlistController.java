package com.boardgameapp.controller;

import com.boardgameapp.dto.AddWishlistItemRequest;
import com.boardgameapp.dto.WishlistItemResponse;
import com.boardgameapp.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/wishlist")
public class MeWishlistController {

    private final WishlistService wishlistService;

    public MeWishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> list(Authentication auth) {
        String username = auth.getName();
        List<WishlistItemResponse> list = wishlistService.listByUsername(username);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<WishlistItemResponse> add(
            Authentication auth,
            @Valid @RequestBody AddWishlistItemRequest request) {
        String username = auth.getName();
        WishlistItemResponse created = wishlistService.add(username, request);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        wishlistService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
