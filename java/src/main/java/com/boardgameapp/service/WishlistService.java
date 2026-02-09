package com.boardgameapp.service;

import com.boardgameapp.dto.AddWishlistItemRequest;
import com.boardgameapp.dto.WishlistItemResponse;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.WishlistItem;
import com.boardgameapp.repository.UserRepository;
import com.boardgameapp.repository.WishlistRepository;
import com.boardgameapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ほしいものリストの一覧・追加・削除を行うサービス。
 */
@Service
public class WishlistService {

    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistService(UserRepository userRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    /**
     * 指定ユーザーのほしいものリストを追加日の降順で取得する。
     *
     * @param username ユーザー名
     * @return ほしいもの一覧
     */
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> listByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * ほしいものリストに1件追加する。
     *
     * @param username ユーザー名
     * @param request ゲーム名・サムネURLなど
     * @return 追加されたアイテム
     */
    @Transactional
    public WishlistItemResponse add(String username, AddWishlistItemRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        WishlistItem entity = new WishlistItem();
        entity.setUserId(user.getId());
        entity.setName(request.getName().trim());
        entity.setThumbnailUrl(blankToNull(request.getThumbnailUrl()));
        entity = wishlistRepository.save(entity);
        return toResponse(entity);
    }

    /**
     * ほしいものリストから指定IDのアイテムを削除する。
     *
     * @param username ユーザー名
     * @param id ほしいものアイテムID
     */
    @Transactional
    public void delete(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        WishlistItem entity = wishlistRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistRepository.delete(entity);
    }

    /** 空文字・空白のみの文字列を null に変換する。 */
    private static String blankToNull(String s) {
        return s != null && s.isBlank() ? null : s;
    }

    /** エンティティをレスポンスDTOに変換する。 */
    private WishlistItemResponse toResponse(WishlistItem e) {
        WishlistItemResponse r = new WishlistItemResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setThumbnailUrl(e.getThumbnailUrl());
        r.setAddedAt(e.getAddedAt());
        return r;
    }
}
