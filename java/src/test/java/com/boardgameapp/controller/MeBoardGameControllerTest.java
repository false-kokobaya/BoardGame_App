package com.boardgameapp.controller;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.service.UserBoardGameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeBoardGameController.class)
@DisplayName("MeBoardGameController")
class MeBoardGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserBoardGameService userBoardGameService;

    private static final String USERNAME = "testuser";

    @Nested
    @DisplayName("GET /api/me/boardgames")
    class ListGames {
        @Test
        @WithMockUser(username = USERNAME)
        void 認証済みなら一覧を返す() throws Exception {
            UserBoardGameResponse resp = new UserBoardGameResponse(
                    1L, null, "カタン", null, null, null, null, null, null, null);
            when(userBoardGameService.listByUsername(eq(USERNAME), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(resp)));

            mockMvc.perform(get("/api/me/boardgames"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("カタン"))
                    .andExpect(jsonPath("$.content[0].id").value(1));

            verify(userBoardGameService).listByUsername(eq(USERNAME), any(Pageable.class));
        }

        @Test
        void 未認証なら401() throws Exception {
            mockMvc.perform(get("/api/me/boardgames"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/me/boardgames")
    class Add {
        @Test
        @WithMockUser(username = USERNAME)
        void 有効なリクエストで201ならず200で作成結果を返す() throws Exception {
            AddBoardGameRequest body = new AddBoardGameRequest();
            body.setName("カルカソンヌ");
            body.setYearPublished(2000);

            UserBoardGameResponse created = new UserBoardGameResponse(
                    10L, null, "カルカソンヌ", null, null, null, null, null, null, Instant.now());
            when(userBoardGameService.add(eq(USERNAME), any(AddBoardGameRequest.class))).thenReturn(created);

            mockMvc.perform(post("/api/me/boardgames")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.name").value("カルカソンヌ"));

            verify(userBoardGameService).add(eq(USERNAME), any(AddBoardGameRequest.class));
        }

        @Test
        @WithMockUser(username = USERNAME)
        void nameが空なら400() throws Exception {
            AddBoardGameRequest body = new AddBoardGameRequest();
            body.setName("");

            mockMvc.perform(post("/api/me/boardgames")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/me/boardgames/{id}")
    class Update {
        @Test
        @WithMockUser(username = USERNAME)
        void 有効なリクエストで更新結果を返す() throws Exception {
            UpdateBoardGameRequest body = new UpdateBoardGameRequest();
            body.setName("カタン 新版");

            UserBoardGameResponse updated = new UserBoardGameResponse(
                    5L, null, "カタン 新版", null, null, null, null, null, null, null);
            when(userBoardGameService.update(eq(USERNAME), eq(5L), any(UpdateBoardGameRequest.class)))
                    .thenReturn(updated);

            mockMvc.perform(put("/api/me/boardgames/5")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("カタン 新版"));

            verify(userBoardGameService).update(eq(USERNAME), eq(5L), any(UpdateBoardGameRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/me/boardgames/{id}")
    class Delete {
        @Test
        @WithMockUser(username = USERNAME)
        void 削除すると204を返す() throws Exception {
            mockMvc.perform(delete("/api/me/boardgames/3").with(csrf()))
                    .andExpect(status().isNoContent());

            verify(userBoardGameService).delete(USERNAME, 3L);
        }
    }

    @Nested
    @DisplayName("GET /api/me/boardgames/{id}")
    class Get {
        @Test
        @WithMockUser(username = USERNAME)
        void 認証済みなら1件取得できる() throws Exception {
            UserBoardGameResponse resp = new UserBoardGameResponse(
                    2L, null, "ディクシット", null, null, null, null, null, null, null);
            when(userBoardGameService.getByIdAndUsername(2L, USERNAME)).thenReturn(resp);

            mockMvc.perform(get("/api/me/boardgames/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("ディクシット"));

            verify(userBoardGameService).getByIdAndUsername(2L, USERNAME);
        }
    }
}
