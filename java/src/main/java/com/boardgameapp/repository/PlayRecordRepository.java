package com.boardgameapp.repository;

import com.boardgameapp.entity.PlayRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/** プレイ記録の永続化を行うリポジトリ。 */
public interface PlayRecordRepository extends JpaRepository<PlayRecord, Long> {

    /** ゲームIDでプレイ記録をプレイ日の降順で取得する。 */
    Page<PlayRecord> findByUserBoardGameIdOrderByPlayedAtDesc(Long userBoardGameId, Pageable pageable);

    /** ユーザーIDでプレイ記録をプレイ日の降順で取得する。 */
    Page<PlayRecord> findByUserIdOrderByPlayedAtDesc(Long userId, Pageable pageable);

    /** ID とユーザーID で1件取得する。 */
    Optional<PlayRecord> findByIdAndUserId(Long id, Long userId);

    /** 指定ゲームに紐づくプレイ記録を一括削除する。 */
    @Modifying
    @Query("DELETE FROM PlayRecord p WHERE p.userBoardGame.id = :userBoardGameId")
    void deleteByUserBoardGameId(@Param("userBoardGameId") Long userBoardGameId);
}
