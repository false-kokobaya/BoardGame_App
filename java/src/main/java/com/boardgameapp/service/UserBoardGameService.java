package com.boardgameapp.service;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.UserBoardGame;
import com.boardgameapp.exception.ResourceNotFoundException;
import com.boardgameapp.repository.UserBoardGameRepository;
import com.boardgameapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザーが所有するボードゲームの一覧・追加・更新・削除・1件取得を行うサービス。
 */
@Service
public class UserBoardGameService {

    private final UserBoardGameRepository userBoardGameRepository;
    private final UserRepository userRepository;

    public UserBoardGameService(UserBoardGameRepository userBoardGameRepository,
                                UserRepository userRepository) {
        this.userBoardGameRepository = userBoardGameRepository;
        this.userRepository = userRepository;
    }

    /**
     * 指定ユーザー名のボードゲーム一覧を追加日時の降順で取得する（ページング対応）。
     *
     * @param username ユーザー名
     * @param pageable ページ・サイズ・ソート
     * @return ボードゲームのページ
     */
    @Transactional(readOnly = true)
    public Page<UserBoardGameResponse> listByUsername(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pageable withSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("addedAt").descending());
        return userBoardGameRepository.findByUserIdOrderByAddedAtDesc(user.getId(), withSort)
                .map(this::toResponse);
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
     * 指定IDのボードゲームを更新する。リクエストで null でないフィールドのみ上書き（部分更新）。
     *
     * @param username ユーザー名
     * @param id ゲームID
     * @param request 更新内容（null のフィールドは変更しない）
     * @return 更新後のゲームのレスポンス
     */
    @Transactional
    public UserBoardGameResponse update(String username, Long id, UpdateBoardGameRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board game not found"));
        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName().trim());
        }
        if (request.getThumbnailUrl() != null) {
            entity.setThumbnailUrl(blankToNull(request.getThumbnailUrl()));
        }
        if (request.getYearPublished() != null) {
            entity.setYearPublished(request.getYearPublished());
        }
        if (request.getMinPlayers() != null) {
            entity.setMinPlayers(request.getMinPlayers());
        }
        if (request.getMaxPlayers() != null) {
            entity.setMaxPlayers(request.getMaxPlayers());
        }
        if (request.getMinPlayTimeMinutes() != null) {
            entity.setMinPlayTimeMinutes(request.getMinPlayTimeMinutes());
        }
        if (request.getMaxPlayTimeMinutes() != null) {
            entity.setMaxPlayTimeMinutes(request.getMaxPlayTimeMinutes());
        }
        entity = userBoardGameRepository.save(entity);
        return toResponse(entity);
    }

    /**
     * 指定IDのボードゲームを削除する。紐づくプレイ記録はエンティティのカスケードで削除される。
     *
     * @param username ユーザー名
     * @param id ゲームID
     */
    @Transactional
    public void delete(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board game not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserBoardGame entity = userBoardGameRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board game not found"));
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
        return new UserBoardGameResponse(
                e.getId(),
                e.getBggId(),
                e.getName(),
                e.getThumbnailUrl(),
                e.getYearPublished(),
                e.getMinPlayers(),
                e.getMaxPlayers(),
                e.getMinPlayTimeMinutes(),
                e.getMaxPlayTimeMinutes(),
                e.getAddedAt());
    }
}
