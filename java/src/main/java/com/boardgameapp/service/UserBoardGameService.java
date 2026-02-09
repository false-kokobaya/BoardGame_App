package com.boardgameapp.service;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.UserBoardGame;
import com.boardgameapp.repository.PlayRecordRepository;
import com.boardgameapp.repository.UserBoardGameRepository;
import com.boardgameapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ユーザーが所有するボードゲームの一覧・追加・更新・削除・1件取得を行うサービス。
 */
@Service
public class UserBoardGameService {

    private final UserBoardGameRepository userBoardGameRepository;
    private final UserRepository userRepository;
    private final PlayRecordRepository playRecordRepository;

    public UserBoardGameService(UserBoardGameRepository userBoardGameRepository,
                                UserRepository userRepository,
                                PlayRecordRepository playRecordRepository) {
        this.userBoardGameRepository = userBoardGameRepository;
        this.userRepository = userRepository;
        this.playRecordRepository = playRecordRepository;
    }

    /**
     * 指定ユーザー名のボードゲーム一覧を追加日時の降順で取得する。
     *
     * @param username ユーザー名
     * @return ボードゲーム一覧
     */
    @Transactional(readOnly = true)
    public List<UserBoardGameResponse> listByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userBoardGameRepository.findByUserIdOrderByAddedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 指定ユーザーにボードゲームを1件追加する。
     *
     * @param username ユーザー名
     * @param request ゲーム名・サムネURL・年など
     * @return 作成されたゲームのレスポンス
     */
    @Transactional
    public UserBoardGameResponse add(String username, AddBoardGameRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame entity = new UserBoardGame();
        entity.setUserId(user.getId());
        entity.setBggId(null);
        entity.setName(request.getName().trim());
        entity.setThumbnailUrl(blankToNull(request.getThumbnailUrl()));
        entity.setYearPublished(request.getYearPublished());
        entity.setMinPlayers(request.getMinPlayers());
        entity.setMaxPlayers(request.getMaxPlayers());
        entity.setMinPlayTimeMinutes(request.getMinPlayTimeMinutes());
        entity.setMaxPlayTimeMinutes(request.getMaxPlayTimeMinutes());
        entity = userBoardGameRepository.save(entity);
        return toResponse(entity);
    }

    /**
     * 指定IDのボードゲームを更新する。リクエストの値をそのまま反映し、null の場合はフィールドをクリアする。
     *
     * @param username ユーザー名
     * @param id ゲームID
     * @param request 更新内容
     * @return 更新後のゲームのレスポンス
     */
    @Transactional
    public UserBoardGameResponse update(String username, Long id, UpdateBoardGameRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Board game not found"));
        // リクエストの値をそのまま反映（null の場合はフィールドをクリア）。name は NOT NULL のため空でなければ更新
        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName().trim());
        }
        entity.setThumbnailUrl(blankToNull(request.getThumbnailUrl()));
        entity.setYearPublished(request.getYearPublished());
        entity.setMinPlayers(request.getMinPlayers());
        entity.setMaxPlayers(request.getMaxPlayers());
        entity.setMinPlayTimeMinutes(request.getMinPlayTimeMinutes());
        entity.setMaxPlayTimeMinutes(request.getMaxPlayTimeMinutes());
        entity = userBoardGameRepository.save(entity);
        return toResponse(entity);
    }

    /**
     * 指定IDのボードゲームを削除する。紐づくプレイ記録も先に削除する。
     *
     * @param username ユーザー名
     * @param id ゲームID
     */
    @Transactional
    public void delete(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Board game not found"));
        playRecordRepository.deleteByUserBoardGameId(entity.getId());
        userBoardGameRepository.delete(entity);
    }

    /**
     * 指定ID・ユーザー名のボードゲームを1件取得する。
     *
     * @param id ゲームID
     * @param username ユーザー名
     * @return ゲームのレスポンス
     */
    @Transactional(readOnly = true)
    public UserBoardGameResponse getByIdAndUsername(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Board game not found"));
        return toResponse(entity);
    }

    /**
     * 空文字・空白のみの文字列を null に変換する。
     */
    private static String blankToNull(String s) {
        return s != null && s.isBlank() ? null : s;
    }

    /** エンティティをレスポンスDTOに変換する。 */
    private UserBoardGameResponse toResponse(UserBoardGame e) {
        UserBoardGameResponse r = new UserBoardGameResponse();
        r.setId(e.getId());
        r.setBggId(e.getBggId());
        r.setName(e.getName());
        r.setThumbnailUrl(e.getThumbnailUrl());
        r.setYearPublished(e.getYearPublished());
        r.setMinPlayers(e.getMinPlayers());
        r.setMaxPlayers(e.getMaxPlayers());
        r.setMinPlayTimeMinutes(e.getMinPlayTimeMinutes());
        r.setMaxPlayTimeMinutes(e.getMaxPlayTimeMinutes());
        r.setAddedAt(e.getAddedAt());
        return r;
    }
}
