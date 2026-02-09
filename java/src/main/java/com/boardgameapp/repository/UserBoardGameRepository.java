package com.boardgameapp.repository;

import com.boardgameapp.entity.UserBoardGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBoardGameRepository extends JpaRepository<UserBoardGame, Long> {

    List<UserBoardGame> findByUserIdOrderByAddedAtDesc(Long userId);

    Optional<UserBoardGame> findByIdAndUserId(Long id, Long userId);
}
