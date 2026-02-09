package com.boardgameapp.service;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.UserBoardGame;
import com.boardgameapp.exception.ResourceNotFoundException;
import com.boardgameapp.repository.UserBoardGameRepository;
import com.boardgameapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserBoardGameService")
class UserBoardGameServiceTest {

    @Mock
    private UserBoardGameRepository userBoardGameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserBoardGameService sut;

    private static final String USERNAME = "testuser";
    private static final Long USER_ID = 1L;
    private User user;
    private UserBoardGame savedGame;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");

        savedGame = new UserBoardGame();
        savedGame.setId(10L);
        savedGame.setUserId(USER_ID);
        savedGame.setName("カタン");
        savedGame.setThumbnailUrl(null);
        savedGame.setYearPublished(1995);
        savedGame.setMinPlayers(3);
        savedGame.setMaxPlayers(4);
    }

    @Nested
    @DisplayName("listByUsername")
    class ListByUsername {
        @Test
        void ユーザーが存在すれば所持ゲーム一覧を返す() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByUserIdOrderByAddedAtDesc(eq(USER_ID), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(savedGame)));

            var result = sut.listByUsername(USERNAME, Pageable.unpaged());

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("カタン");
            assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
            assertThat(result.getContent().get(0).getYearPublished()).isEqualTo(1995);
        }

        @Test
        void ユーザーが存在しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.listByUsername(USERNAME, Pageable.unpaged()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    @Nested
    @DisplayName("add")
    class Add {
        @Test
        void リクエストの内容でゲームを追加しレスポンスを返す() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.save(any(UserBoardGame.class))).thenAnswer(inv -> {
                UserBoardGame e = inv.getArgument(0);
                e.setId(99L);
                return e;
            });

            AddBoardGameRequest request = new AddBoardGameRequest();
            request.setName(" カルカソンヌ ");
            request.setThumbnailUrl("https://example.com/img.png");
            request.setYearPublished(2000);
            request.setMinPlayers(2);
            request.setMaxPlayers(5);

            UserBoardGameResponse result = sut.add(USERNAME, request);

            ArgumentCaptor<UserBoardGame> captor = ArgumentCaptor.forClass(UserBoardGame.class);
            verify(userBoardGameRepository).save(captor.capture());
            UserBoardGame saved = captor.getValue();
            assertThat(saved.getUserId()).isEqualTo(USER_ID);
            assertThat(saved.getName()).isEqualTo("カルカソンヌ");
            assertThat(saved.getThumbnailUrl()).isEqualTo("https://example.com/img.png");
            assertThat(saved.getYearPublished()).isEqualTo(2000);
            assertThat(saved.getMinPlayers()).isEqualTo(2);
            assertThat(saved.getMaxPlayers()).isEqualTo(5);

            assertThat(result.getName()).isEqualTo("カルカソンヌ");
            assertThat(result.getId()).isEqualTo(99L);
        }

        @Test
        void 空のthumbnailUrlはnullで保存する() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.save(any(UserBoardGame.class))).thenAnswer(inv -> {
                UserBoardGame e = inv.getArgument(0);
                e.setId(1L);
                return e;
            });

            AddBoardGameRequest request = new AddBoardGameRequest();
            request.setName("test");
            request.setThumbnailUrl("   ");

            sut.add(USERNAME, request);

            ArgumentCaptor<UserBoardGame> captor = ArgumentCaptor.forClass(UserBoardGame.class);
            verify(userBoardGameRepository).save(captor.capture());
            assertThat(captor.getValue().getThumbnailUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        void 自分のゲームを更新できる() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(10L, USER_ID))
                    .thenReturn(Optional.of(savedGame));
            when(userBoardGameRepository.save(any(UserBoardGame.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBoardGameRequest request = new UpdateBoardGameRequest();
            request.setName("カタン 新版");
            request.setThumbnailUrl(null);
            request.setYearPublished(null);

            UserBoardGameResponse result = sut.update(USERNAME, 10L, request);

            ArgumentCaptor<UserBoardGame> captor = ArgumentCaptor.forClass(UserBoardGame.class);
            verify(userBoardGameRepository).save(captor.capture());
            UserBoardGame updated = captor.getValue();
            assertThat(updated.getName()).isEqualTo("カタン 新版");
            // 部分更新: null のフィールドは変更しない（thumbnailUrl/yearPublished はそのまま）
            assertThat(updated.getThumbnailUrl()).isNull();
            assertThat(updated.getYearPublished()).isEqualTo(1995);
            assertThat(result.getName()).isEqualTo("カタン 新版");
        }

        @Test
        void ゲームが存在しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(999L, USER_ID)).thenReturn(Optional.empty());

            UpdateBoardGameRequest request = new UpdateBoardGameRequest();
            request.setName("x");

            assertThatThrownBy(() -> sut.update(USERNAME, 999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Board game not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        void 自分のゲームを削除できる_紐づくプレイ記録はカスケードで削除される() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(10L, USER_ID))
                    .thenReturn(Optional.of(savedGame));

            sut.delete(USERNAME, 10L);

            verify(userBoardGameRepository).delete(savedGame);
        }
    }

    @Nested
    @DisplayName("getByIdAndUsername")
    class GetByIdAndUsername {
        @Test
        void 自分のゲームを1件取得できる() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(10L, USER_ID))
                    .thenReturn(Optional.of(savedGame));

            UserBoardGameResponse result = sut.getByIdAndUsername(10L, USERNAME);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getName()).isEqualTo("カタン");
        }
    }
}
