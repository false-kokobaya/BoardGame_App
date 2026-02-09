package com.boardgameapp.service;

import com.boardgameapp.dto.AddWishlistItemRequest;
import com.boardgameapp.dto.WishlistItemResponse;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.WishlistItem;
import com.boardgameapp.repository.UserRepository;
import com.boardgameapp.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistService(UserRepository userRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> listByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistItemResponse add(String username, AddWishlistItemRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishlistItem entity = new WishlistItem();
        entity.setUserId(user.getId());
        entity.setName(request.getName().trim());
        entity.setThumbnailUrl(blankToNull(request.getThumbnailUrl()));
        entity = wishlistRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public void delete(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishlistItem entity = wishlistRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        wishlistRepository.delete(entity);
    }

    private static String blankToNull(String s) {
        return s != null && s.isBlank() ? null : s;
    }

    private WishlistItemResponse toResponse(WishlistItem e) {
        WishlistItemResponse r = new WishlistItemResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setThumbnailUrl(e.getThumbnailUrl());
        r.setAddedAt(e.getAddedAt());
        return r;
    }
}
