package com.boardgameapp.repository;

import com.boardgameapp.entity.PlayRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** プレイ記録の永続化を行うリポジトリ。 */
public interface PlayRecordRepository extends JpaRepository<PlayRecord, Long> {

    /** ゲームIDでプレイ記録をプレイ日の降順で取得する。 */
    List<PlayRecord> findByUserBoardGameIdOrderByPlayedAtDesc(Long userBoardGameId);

    /** ユーザーIDでプレイ記録をプレイ日の降順で取得する。 */
    List<PlayRecord> findByUserIdOrderByPlayedAtDesc(Long userId);

    /** ID とユーザーID で1件取得する。 */
    Optional<PlayRecord> findByIdAndUserId(Long id, Long userId);

    /** 指定ゲームに紐づくプレイ記録を一括削除する。 */
    void deleteByUserBoardGameId(Long userBoardGameId);
}
