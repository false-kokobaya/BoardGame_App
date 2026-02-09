package com.boardgameapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** プレイ記録の追加・更新APIのリクエスト。 */
public class PlayRecordRequest {

    @NotNull(message = "Play date is required")
    private LocalDate playedAt;

    @Size(max = 2000)
    private String memo;

    @NotNull(message = "Player count is required")
    @Min(value = 1, message = "Player count must be at least 1")
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
