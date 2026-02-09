package com.boardgameapp.repository;

import com.boardgameapp.entity.PlayRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayRecordRepository extends JpaRepository<PlayRecord, Long> {

    List<PlayRecord> findByUserBoardGameIdOrderByPlayedAtDesc(Long userBoardGameId);

    List<PlayRecord> findByUserIdOrderByPlayedAtDesc(Long userId);

    Optional<PlayRecord> findByIdAndUserId(Long id, Long userId);

    void deleteByUserBoardGameId(Long userBoardGameId);
}
