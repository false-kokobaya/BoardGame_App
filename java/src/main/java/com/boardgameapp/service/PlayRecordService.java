package com.boardgameapp.service;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.entity.PlayRecord;
import com.boardgameapp.exception.ResourceNotFoundException;
import com.boardgameapp.entity.User;
import com.boardgameapp.entity.UserBoardGame;
import com.boardgameapp.repository.PlayRecordRepository;
import com.boardgameapp.repository.UserBoardGameRepository;
import com.boardgameapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * プレイ記録の一覧・追加・更新・削除を行うサービス。
 */
@Service
public class PlayRecordService {

    private final PlayRecordRepository playRecordRepository;
    private final UserRepository userRepository;
    private final UserBoardGameRepository userBoardGameRepository;

    public PlayRecordService(PlayRecordRepository playRecordRepository,
                             UserRepository userRepository,
                             UserBoardGameRepository userBoardGameRepository) {
        this.playRecordRepository = playRecordRepository;
        this.userRepository = userRepository;
        this.userBoardGameRepository = userBoardGameRepository;
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * 指定ゲームに紐づくプレイ記録一覧をプレイ日の降順で取得する（ページング対応）。
     *
     * @param username ユーザー名
     * @param userBoardGameId ユーザー所持ゲームID
     * @param pageable ページ・サイズ
     * @return プレイ記録のページ
     */
    @Transactional(readOnly = true)
    public Page<PlayRecordResponse> listByUserBoardGame(String username, Long userBoardGameId, Pageable pageable) {
        User user = findUserOrThrow(username);
        UserBoardGame ubg = userBoardGameRepository.findByIdAndUserId(userBoardGameId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board game not found"));
        return playRecordRepository.findByUserBoardGameIdOrderByPlayedAtDesc(ubg.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * 指定ユーザーのプレイ記録をプレイ日の降順で取得する（ページング対応）。
     *
     * @param username ユーザー名
     * @param pageable ページ・サイズ
     * @return プレイ記録のページ
     */
    @Transactional(readOnly = true)
    public Page<PlayRecordResponse> listAllByUsername(String username, Pageable pageable) {
        User user = findUserOrThrow(username);
        return playRecordRepository.findByUserIdOrderByPlayedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * 指定ゲームにプレイ記録を1件追加する。
     *
     * @param username ユーザー名
     * @param userBoardGameId ユーザー所持ゲームID
     * @param request プレイ日・メモ・人数
     * @return 作成されたプレイ記録
     */
    @Transactional
    public PlayRecordResponse add(String username, Long userBoardGameId, PlayRecordRequest request) {
        User user = findUserOrThrow(username);
        UserBoardGame ubg = userBoardGameRepository.findByIdAndUserId(userBoardGameId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board game not found"));
        PlayRecord record = new PlayRecord();
        record.setUserId(user.getId());
        record.setUserBoardGameId(ubg.getId());
        record.setPlayedAt(request.getPlayedAt());
        record.setMemo(request.getMemo());
        record.setPlayerCount(request.getPlayerCount());
        record = playRecordRepository.save(record);
        return toResponse(record);
    }

    /**
     * 指定IDのプレイ記録を更新する。URL の userBoardGameId と記録のゲームIDが一致しない場合は 404。
     *
     * @param username ユーザー名
     * @param userBoardGameId URL のゲームID（整合性チェック用）
     * @param playRecordId プレイ記録ID
     * @param request 更新内容
     * @return 更新後のプレイ記録
     */
    @Transactional
    public PlayRecordResponse update(String username, Long userBoardGameId, Long playRecordId, PlayRecordRequest request) {
        User user = findUserOrThrow(username);
        PlayRecord record = playRecordRepository.findByIdAndUserId(playRecordId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Play record not found"));
        if (!record.getUserBoardGameId().equals(userBoardGameId)) {
            throw new ResourceNotFoundException("Play record not found");
        }
        record.setPlayedAt(request.getPlayedAt());
        record.setMemo(request.getMemo());
        record.setPlayerCount(request.getPlayerCount());
        record = playRecordRepository.save(record);
        return toResponse(record);
    }

    /**
     * 指定IDのプレイ記録を削除する。URL の userBoardGameId と記録のゲームIDが一致しない場合は 404。
     *
     * @param username ユーザー名
     * @param userBoardGameId URL のゲームID（整合性チェック用）
     * @param playRecordId プレイ記録ID
     */
    @Transactional
    public void delete(String username, Long userBoardGameId, Long playRecordId) {
        User user = findUserOrThrow(username);
        PlayRecord record = playRecordRepository.findByIdAndUserId(playRecordId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Play record not found"));
        if (!record.getUserBoardGameId().equals(userBoardGameId)) {
            throw new ResourceNotFoundException("Play record not found");
        }
        playRecordRepository.delete(record);
    }

    /** エンティティをレスポンスDTOに変換する。 */
    private PlayRecordResponse toResponse(PlayRecord r) {
        PlayRecordResponse res = new PlayRecordResponse();
        res.setId(r.getId());
        res.setUserBoardGameId(r.getUserBoardGameId());
        res.setPlayedAt(r.getPlayedAt());
        res.setMemo(r.getMemo());
        res.setPlayerCount(r.getPlayerCount());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }
}
