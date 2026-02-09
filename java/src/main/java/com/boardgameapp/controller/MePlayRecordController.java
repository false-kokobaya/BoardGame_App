package com.boardgameapp.controller;

import com.boardgameapp.dto.PlayRecordRequest;
import com.boardgameapp.dto.PlayRecordResponse;
import com.boardgameapp.service.PlayRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class MePlayRecordController {

    private final PlayRecordService playRecordService;

    public MePlayRecordController(PlayRecordService playRecordService) {
        this.playRecordService = playRecordService;
    }

    @GetMapping("/boardgames/{userBoardGameId}/plays")
    public ResponseEntity<List<PlayRecordResponse>> listByGame(
            Authentication auth,
            @PathVariable Long userBoardGameId) {
        String username = auth.getName();
        List<PlayRecordResponse> list = playRecordService.listByUserBoardGame(username, userBoardGameId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/plays")
    public ResponseEntity<List<PlayRecordResponse>> listAll(Authentication auth) {
        String username = auth.getName();
        List<PlayRecordResponse> list = playRecordService.listAllByUsername(username);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/boardgames/{userBoardGameId}/plays")
    public ResponseEntity<PlayRecordResponse> add(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @Valid @RequestBody PlayRecordRequest request) {
        String username = auth.getName();
        PlayRecordResponse created = playRecordService.add(username, userBoardGameId, request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/boardgames/{userBoardGameId}/plays/{id}")
    public ResponseEntity<PlayRecordResponse> update(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @PathVariable Long id,
            @Valid @RequestBody PlayRecordRequest request) {
        String username = auth.getName();
        PlayRecordResponse updated = playRecordService.update(username, id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/boardgames/{userBoardGameId}/plays/{id}")
    public ResponseEntity<Void> delete(
            Authentication auth,
            @PathVariable Long userBoardGameId,
            @PathVariable Long id) {
        String username = auth.getName();
        playRecordService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
