package com.boardgameapp.dto;

import java.time.Instant;

public class UserBoardGameResponse {

    private Long id;
    private String bggId;
    private String name;
    private String thumbnailUrl;
    private Integer yearPublished;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer minPlayTimeMinutes;
    private Integer maxPlayTimeMinutes;
    private Instant addedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBggId() {
        return bggId;
    }

    public void setBggId(String bggId) {
        this.bggId = bggId;
    }

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

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
}
