package com.boardgameapp.controller;

import com.boardgameapp.dto.AddBoardGameRequest;
import com.boardgameapp.dto.UpdateBoardGameRequest;
import com.boardgameapp.dto.UserBoardGameResponse;
import com.boardgameapp.service.UserBoardGameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/boardgames")
public class MeBoardGameController {

    private final UserBoardGameService userBoardGameService;

    public MeBoardGameController(UserBoardGameService userBoardGameService) {
        this.userBoardGameService = userBoardGameService;
    }

    @GetMapping
    public ResponseEntity<List<UserBoardGameResponse>> list(Authentication auth) {
        String username = auth.getName();
        List<UserBoardGameResponse> list = userBoardGameService.listByUsername(username);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<UserBoardGameResponse> add(
            Authentication auth,
            @Valid @RequestBody AddBoardGameRequest request) {
        String username = auth.getName();
        UserBoardGameResponse created = userBoardGameService.add(username, request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBoardGameResponse> update(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardGameRequest request) {
        String username = auth.getName();
        UserBoardGameResponse updated = userBoardGameService.update(username, id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        userBoardGameService.delete(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBoardGameResponse> get(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        UserBoardGameResponse response = userBoardGameService.getByIdAndUsername(id, username);
        return ResponseEntity.ok(response);
    }
}
