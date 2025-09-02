package com.list.todo.post.board.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.global.exception.LoginException;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid BoardRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(boardService.createBoard(request, user));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<?> get(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @GetMapping
    public ResponseEntity<?> getBoards(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardService.getBoardList(page, size));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> update(@PathVariable Long boardId,
                                    @RequestBody BoardRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(@PathVariable Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
//
//    // 좋아요/싫어요 처리
//    @PostMapping("/{boardId}/like/{type}")
//    public ResponseEntity<?> handleLike(@PathVariable Long boardId,
//                                        @PathVariable String type,
//                                        @AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(boardService.handleLike(boardId, userDetails.getUsername(), LikeEntity.LikeType.valueOf(type.toUpperCase())));
//    }
}
