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

    @Transactional(readOnly = true)
    public List<PlayRecordResponse> listAllByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return playRecordRepository.findByUserIdOrderByPlayedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void delete(String username, Long playRecordId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PlayRecord record = playRecordRepository.findByIdAndUserId(playRecordId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Play record not found"));
        playRecordRepository.delete(record);
    }

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
