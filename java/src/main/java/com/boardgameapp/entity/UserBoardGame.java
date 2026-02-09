package com.boardgameapp.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * ユーザーが所持するボードゲーム1件を表すエンティティ。
 */
@Entity
@Table(name = "user_board_games")
public class UserBoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bgg_id", length = 20)
    private String bggId;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(length = 1000)
    private String thumbnailUrl;

    private Integer yearPublished;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer minPlayTimeMinutes;
    private Integer maxPlayTimeMinutes;

    @Column(nullable = false, updatable = false)
    private Instant addedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

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
