package com.boardgameapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;

/**
 * 1回のプレイ記録を表すエンティティ。
 */
@Entity
@Table(name = "play_records")
public class PlayRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_board_game_id", nullable = false)
    private Long userBoardGameId;

    @Column(nullable = false)
    private LocalDate playedAt;

    @Column(length = 2000)
    private String memo;

    @NotNull
    @Min(1)
    private Integer playerCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void setCreatedAtIfNull() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_board_game_id", insertable = false, updatable = false)
    private UserBoardGame userBoardGame;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserBoardGameId() {
        return userBoardGameId;
    }

    public void setUserBoardGameId(Long userBoardGameId) {
        this.userBoardGameId = userBoardGameId;
    }

    public LocalDate getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDate playedAt) {
        this.playedAt = playedAt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
