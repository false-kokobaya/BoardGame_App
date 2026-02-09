package com.boardgameapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** ボードゲーム追加APIのリクエスト。 */
public class AddBoardGameRequest extends BoardGameBaseRequest {

    @NotBlank(message = "Game name is required")
    @Size(max = 500)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
