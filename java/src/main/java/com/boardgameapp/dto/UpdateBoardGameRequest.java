package com.boardgameapp.dto;

import jakarta.validation.constraints.Size;

/** ボードゲーム更新APIのリクエスト。 */
public class UpdateBoardGameRequest {

    @Size(max = 500)
    private String name;

    @Size(max = 1000)
    private String thumbnailUrl;

    private Integer yearPublished;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer minPlayTimeMinutes;
    private Integer maxPlayTimeMinutes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
