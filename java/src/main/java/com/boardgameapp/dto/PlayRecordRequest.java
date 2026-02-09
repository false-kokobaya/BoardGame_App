package com.boardgameapp.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PlayRecordRequest {

    @NotNull(message = "Play date is required")
    private LocalDate playedAt;

    private String memo;
    private Integer playerCount;

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
}
