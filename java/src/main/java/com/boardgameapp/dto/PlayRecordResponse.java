package com.boardgameapp.dto;

import java.time.Instant;
import java.time.LocalDate;

/** プレイ記録1件のAPIレスポンス。 */
public class PlayRecordResponse {

    private Long id;
    private Long userBoardGameId;
    private LocalDate playedAt;
    private String memo;
    private Integer playerCount;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
