package com.boardgameapp.service;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.entity.PlayRecord;
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

    /**
     * 指定ゲームに紐づくプレイ記録一覧をプレイ日の降順で取得する。
     *
     * @param username ユーザー名
     * @param userBoardGameId ユーザー所持ゲームID
     * @return プレイ記録一覧
     */
    @Transactional(readOnly = true)
    public List<PlayRecordResponse> listByUserBoardGame(String username, Long userBoardGameId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame ubg = userBoardGameRepository.findByIdAndUserId(userBoardGameId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Board game not found"));
        return playRecordRepository.findByUserBoardGameIdOrderByPlayedAtDesc(ubg.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 指定ユーザーの全プレイ記録をプレイ日の降順で取得する。
     *
     * @param username ユーザー名
     * @return プレイ記録一覧
     */
    @Transactional(readOnly = true)
    public List<PlayRecordResponse> listAllByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return playRecordRepository.findByUserIdOrderByPlayedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserBoardGame ubg = userBoardGameRepository.findByIdAndUserId(userBoardGameId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Board game not found"));
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
     * 指定IDのプレイ記録を更新する。
     *
     * @param username ユーザー名
     * @param playRecordId プレイ記録ID
     * @param request 更新内容
     * @return 更新後のプレイ記録
     */
    @Transactional
    public PlayRecordResponse update(String username, Long playRecordId, PlayRecordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PlayRecord record = playRecordRepository.findByIdAndUserId(playRecordId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Play record not found"));
        record.setPlayedAt(request.getPlayedAt());
        record.setMemo(request.getMemo());
        record.setPlayerCount(request.getPlayerCount());
        record = playRecordRepository.save(record);
        return toResponse(record);
    }

    /**
     * 指定IDのプレイ記録を削除する。
     *
     * @param username ユーザー名
     * @param playRecordId プレイ記録ID
     */
    @Transactional
    public void delete(String username, Long playRecordId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PlayRecord record = playRecordRepository.findByIdAndUserId(playRecordId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Play record not found"));
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
