package com.boardgameapp.dto;

import java.time.Instant;

/** ボードゲーム1件のAPIレスポンス。 */
public record UserBoardGameResponse(
        Long id,
        String bggId,
        String name,
        String thumbnailUrl,
        Integer yearPublished,
        Integer minPlayers,
        Integer maxPlayers,
        Integer minPlayTimeMinutes,
        Integer maxPlayTimeMinutes,
        Instant addedAt) {
}
