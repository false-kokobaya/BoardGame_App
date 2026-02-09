package com.boardgameapp.controller;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.security.JwtUtil;
import com.boardgameapp.service.PlayRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MePlayRecordController.class)
@DisplayName("MePlayRecordController")
class MePlayRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayRecordService playRecordService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;

    private static final String USERNAME = "testuser";
    private static final Long GAME_ID = 10L;
    private static final Long PLAY_ID = 100L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("GET /api/me/boardgames/{userBoardGameId}/plays")
    class ListByGame {
        @Test
        @WithMockUser(username = USERNAME)
        void 認証済みならプレイ記録一覧を返す() throws Exception {
            PlayRecordResponse resp = new PlayRecordResponse();
            resp.setId(PLAY_ID);
            resp.setUserBoardGameId(GAME_ID);
            resp.setPlayedAt(LocalDate.of(2024, 1, 15));
            resp.setMemo("メモ");
            resp.setPlayerCount(4);
            when(playRecordService.listByUserBoardGame(USERNAME, GAME_ID)).thenReturn(List.of(resp));

            mockMvc.perform(get("/api/me/boardgames/" + GAME_ID + "/plays"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(100))
                    .andExpect(jsonPath("$[0].memo").value("メモ"))
                    .andExpect(jsonPath("$[0].playerCount").value(4));

            verify(playRecordService).listByUserBoardGame(USERNAME, GAME_ID);
        }

        @Test
        void 未認証なら401() throws Exception {
            mockMvc.perform(get("/api/me/boardgames/" + GAME_ID + "/plays"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/me/boardgames/{userBoardGameId}/plays")
    class Add {
        @Test
        @WithMockUser(username = USERNAME)
        void 有効なリクエストでプレイ記録を追加できる() throws Exception {
            PlayRecordRequest body = new PlayRecordRequest();
            body.setPlayedAt(LocalDate.of(2024, 2, 1));
            body.setMemo("追加メモ");
            body.setPlayerCount(3);

            PlayRecordResponse created = new PlayRecordResponse();
            created.setId(101L);
            created.setUserBoardGameId(GAME_ID);
            created.setPlayedAt(LocalDate.of(2024, 2, 1));
            when(playRecordService.add(eq(USERNAME), eq(GAME_ID), ArgumentMatchers.any(PlayRecordRequest.class)))
                    .thenReturn(created);

            mockMvc.perform(post("/api/me/boardgames/" + GAME_ID + "/plays")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/plays/101")))
                    .andExpect(jsonPath("$.id").value(101))
                    .andExpect(jsonPath("$.userBoardGameId").value(10));

            verify(playRecordService).add(eq(USERNAME), eq(GAME_ID), ArgumentMatchers.any(PlayRecordRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/me/boardgames/{userBoardGameId}/plays/{id}")
    class Update {
        @Test
        @WithMockUser(username = USERNAME)
        void 有効なリクエストでプレイ記録を更新できる() throws Exception {
            PlayRecordRequest body = new PlayRecordRequest();
            body.setPlayedAt(LocalDate.of(2024, 1, 20));
            body.setMemo("更新メモ");

            PlayRecordResponse updated = new PlayRecordResponse();
            updated.setId(PLAY_ID);
            updated.setMemo("更新メモ");
            when(playRecordService.update(eq(USERNAME), eq(GAME_ID), eq(PLAY_ID), ArgumentMatchers.any(PlayRecordRequest.class)))
                    .thenReturn(updated);

            mockMvc.perform(put("/api/me/boardgames/" + GAME_ID + "/plays/" + PLAY_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.memo").value("更新メモ"));

            verify(playRecordService).update(eq(USERNAME), eq(GAME_ID), eq(PLAY_ID), ArgumentMatchers.any(PlayRecordRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/me/boardgames/{userBoardGameId}/plays/{id}")
    class Delete {
        @Test
        @WithMockUser(username = USERNAME)
        void 削除すると204を返す() throws Exception {
            mockMvc.perform(delete("/api/me/boardgames/" + GAME_ID + "/plays/" + PLAY_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(playRecordService).delete(USERNAME, GAME_ID, PLAY_ID);
        }
    }
}
