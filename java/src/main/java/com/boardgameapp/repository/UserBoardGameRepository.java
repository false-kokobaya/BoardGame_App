package com.boardgameapp.repository;

import com.boardgameapp.entity.UserBoardGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** ユーザー所持ボードゲームの永続化を行うリポジトリ。 */
public interface UserBoardGameRepository extends JpaRepository<UserBoardGame, Long> {

    /** ユーザーIDで一覧を追加日の降順で取得する（ページング対応）。 */
    Page<UserBoardGame> findByUserIdOrderByAddedAtDesc(Long userId, Pageable pageable);

    /** ID とユーザーID で1件取得する。 */
    Optional<UserBoardGame> findByIdAndUserId(Long id, Long userId);
}
