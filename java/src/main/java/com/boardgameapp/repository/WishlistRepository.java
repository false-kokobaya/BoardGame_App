package com.boardgameapp.repository;

import com.boardgameapp.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** ほしいものリストの永続化を行うリポジトリ。 */
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    /** ユーザーIDで一覧を追加日の降順で取得する。 */
    List<WishlistItem> findByUserIdOrderByAddedAtDesc(Long userId);

    /** ID とユーザーID で1件取得する。 */
    Optional<WishlistItem> findByIdAndUserId(Long id, Long userId);
}
