package com.boardgameapp.dto;

import jakarta.validation.constraints.Size;

/**
 * ボードゲーム更新APIのリクエスト。
 * 部分更新: null のフィールドは変更せず、非 null のフィールドのみ上書きする。
 */
public class UpdateBoardGameRequest extends BoardGameBaseRequest {

    @Size(max = 500)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
