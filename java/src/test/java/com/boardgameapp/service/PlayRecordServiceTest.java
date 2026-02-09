package com.boardgameapp.service;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.entity.PlayRecord;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.UserBoardGame;
import com.boardgameapp.exception.ResourceNotFoundException;
import com.boardgameapp.repository.PlayRecordRepository;
import com.boardgameapp.repository.UserBoardGameRepository;
import com.boardgameapp.repository.UserRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayRecordService")
class PlayRecordServiceTest {

    @Mock
    private PlayRecordRepository playRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBoardGameRepository userBoardGameRepository;

    @InjectMocks
    private PlayRecordService sut;

    private static final String USERNAME = "testuser";
    private static final Long USER_ID = 1L;
    private static final Long GAME_ID = 10L;
    private User user;
    private UserBoardGame userBoardGame;
    private PlayRecord savedRecord;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");

        userBoardGame = new UserBoardGame();
        userBoardGame.setId(GAME_ID);
        userBoardGame.setUserId(USER_ID);
        userBoardGame.setName("カタン");

        savedRecord = new PlayRecord();
        savedRecord.setId(100L);
        savedRecord.setUserId(USER_ID);
        savedRecord.setUserBoardGameId(GAME_ID);
        savedRecord.setPlayedAt(LocalDate.of(2024, 1, 15));
        savedRecord.setMemo("楽しかった");
        savedRecord.setPlayerCount(4);
    }

    @Nested
    @DisplayName("listByUserBoardGame")
    class ListByUserBoardGame {
        @Test
        void ゲームに紐づくプレイ記録一覧を返す() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(GAME_ID, USER_ID))
                    .thenReturn(Optional.of(userBoardGame));
            when(playRecordRepository.findByUserBoardGameIdOrderByPlayedAtDesc(eq(GAME_ID), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(savedRecord)));

            var result = sut.listByUserBoardGame(USERNAME, GAME_ID, Pageable.unpaged());

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
            assertThat(result.getContent().get(0).getPlayedAt()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(result.getContent().get(0).getMemo()).isEqualTo("楽しかった");
            assertThat(result.getContent().get(0).getPlayerCount()).isEqualTo(4);
        }

        @Test
        void ゲームが存在しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(GAME_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.listByUserBoardGame(USERNAME, GAME_ID, Pageable.unpaged()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Board game not found");
        }
    }

    @Nested
    @DisplayName("add")
    class Add {
        @Test
        void プレイ記録を追加できる() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(userBoardGameRepository.findByIdAndUserId(GAME_ID, USER_ID))
                    .thenReturn(Optional.of(userBoardGame));
            when(playRecordRepository.save(any(PlayRecord.class))).thenAnswer(inv -> {
                PlayRecord r = inv.getArgument(0);
                r.setId(101L);
                return r;
            });

            PlayRecordRequest request = new PlayRecordRequest();
            request.setPlayedAt(LocalDate.of(2024, 2, 1));
            request.setMemo("メモ");
            request.setPlayerCount(3);

            PlayRecordResponse result = sut.add(USERNAME, GAME_ID, request);

            ArgumentCaptor<PlayRecord> captor = ArgumentCaptor.forClass(PlayRecord.class);
            verify(playRecordRepository).save(captor.capture());
            PlayRecord saved = captor.getValue();
            assertThat(saved.getUserId()).isEqualTo(USER_ID);
            assertThat(saved.getUserBoardGameId()).isEqualTo(GAME_ID);
            assertThat(saved.getPlayedAt()).isEqualTo(LocalDate.of(2024, 2, 1));
            assertThat(saved.getMemo()).isEqualTo("メモ");
            assertThat(saved.getPlayerCount()).isEqualTo(3);
            assertThat(result.getId()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        void 自分のプレイ記録を更新できる() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(100L, USER_ID))
                    .thenReturn(Optional.of(savedRecord));
            when(playRecordRepository.save(any(PlayRecord.class))).thenAnswer(inv -> inv.getArgument(0));

            PlayRecordRequest request = new PlayRecordRequest();
            request.setPlayedAt(LocalDate.of(2024, 1, 20));
            request.setMemo("更新メモ");
            request.setPlayerCount(2);

            PlayRecordResponse result = sut.update(USERNAME, GAME_ID, 100L, request);

            ArgumentCaptor<PlayRecord> captor = ArgumentCaptor.forClass(PlayRecord.class);
            verify(playRecordRepository).save(captor.capture());
            assertThat(captor.getValue().getPlayedAt()).isEqualTo(LocalDate.of(2024, 1, 20));
            assertThat(captor.getValue().getMemo()).isEqualTo("更新メモ");
            assertThat(captor.getValue().getPlayerCount()).isEqualTo(2);
            assertThat(result.getMemo()).isEqualTo("更新メモ");
        }

        @Test
        void 記録が存在しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(999L, USER_ID)).thenReturn(Optional.empty());

            PlayRecordRequest request = new PlayRecordRequest();
            request.setPlayedAt(LocalDate.now());

            assertThatThrownBy(() -> sut.update(USERNAME, GAME_ID, 999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Play record not found");
        }

        @Test
        void userBoardGameIdが一致しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(100L, USER_ID))
                    .thenReturn(Optional.of(savedRecord));

            PlayRecordRequest request = new PlayRecordRequest();
            request.setPlayedAt(LocalDate.now());

            assertThatThrownBy(() -> sut.update(USERNAME, 999L, 100L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Play record not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        void 自分のプレイ記録を削除できる() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(100L, USER_ID))
                    .thenReturn(Optional.of(savedRecord));

            sut.delete(USERNAME, GAME_ID, 100L);

            verify(playRecordRepository).delete(savedRecord);
        }

        @Test
        void 記録が存在しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(999L, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.delete(USERNAME, GAME_ID, 999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Play record not found");
        }

        @Test
        void userBoardGameIdが一致しなければResourceNotFoundException() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(playRecordRepository.findByIdAndUserId(100L, USER_ID))
                    .thenReturn(Optional.of(savedRecord));

            assertThatThrownBy(() -> sut.delete(USERNAME, 999L, 100L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Play record not found");
        }
    }
}
