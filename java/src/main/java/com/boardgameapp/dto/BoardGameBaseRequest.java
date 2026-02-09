package com.boardgameapp.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * ボードゲームの name 以外の共通リクエストフィールドとバリデーション。
 * AddBoardGameRequest / UpdateBoardGameRequest の基底。
 */
@Getter
@Setter
public abstract class BoardGameBaseRequest {

    @Size(max = 1000)
    @Pattern(regexp = "^(https?://.*)?$", message = "サムネイル URL は空か http/https スキームで指定してください")
    private String thumbnailUrl;

    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
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

    /** 人数・プレイ時間の範囲整合性（min <= max）。 */
    @AssertTrue(message = "Min players must not exceed max players")
    public boolean isPlayersRangeValid() {
        if (minPlayers == null || maxPlayers == null) return true;
        return minPlayers <= maxPlayers;
    }

    /** プレイ時間の範囲整合性（min <= max）。 */
    @AssertTrue(message = "Min play time must not exceed max play time")
    public boolean isPlayTimeRangeValid() {
        if (minPlayTimeMinutes == null || maxPlayTimeMinutes == null) return true;
        return minPlayTimeMinutes <= maxPlayTimeMinutes;
    }
}
