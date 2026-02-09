package com.boardgameapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * ボードゲームの name 以外の共通リクエストフィールドとバリデーション。
 * AddBoardGameRequest / UpdateBoardGameRequest の基底。
 */
public abstract class BoardGameBaseRequest {

    @Size(max = 1000)
    private String thumbnailUrl;

    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2030, message = "Year must be 2030 or earlier")
    private Integer yearPublished;

    @Min(value = 1, message = "Min players must be at least 1")
    @Max(value = 100, message = "Min players must be at most 100")
    private Integer minPlayers;

    @Min(value = 1, message = "Max players must be at least 1")
    @Max(value = 100, message = "Max players must be at most 100")
    private Integer maxPlayers;

    @Min(value = 1, message = "Min play time must be at least 1 minute")
    @Max(value = 1440, message = "Min play time must be at most 1440 minutes")
    private Integer minPlayTimeMinutes;

    @Min(value = 1, message = "Max play time must be at least 1 minute")
    @Max(value = 1440, message = "Max play time must be at most 1440 minutes")
    private Integer maxPlayTimeMinutes;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getMinPlayTimeMinutes() {
        return minPlayTimeMinutes;
    }

    public void setMinPlayTimeMinutes(Integer minPlayTimeMinutes) {
        this.minPlayTimeMinutes = minPlayTimeMinutes;
    }

    public Integer getMaxPlayTimeMinutes() {
        return maxPlayTimeMinutes;
    }

    public void setMaxPlayTimeMinutes(Integer maxPlayTimeMinutes) {
        this.maxPlayTimeMinutes = maxPlayTimeMinutes;
    }
}
