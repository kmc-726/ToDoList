package com.list.todo.post.board.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.security.CustomUserDetails;
import com.list.todo.global.exception.LoginException;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.dto.BoardResponse;
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
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(boardService.createBoard(request, user));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String loginId = userDetails != null ? userDetails.getUsername() : null;
        BoardResponse board = boardService.getBoard(boardId, loginId);
        return ResponseEntity.ok(board);
    }

    @GetMapping
    public ResponseEntity<?> getBoards(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        String loginId = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(boardService.getBoardList(page, size, loginId));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> update(@PathVariable Long boardId,
                                    @RequestBody BoardRequest request,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, request, userDetails.getId()));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(@PathVariable Long boardId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boardService.deleteBoard(boardId, userId, isAdmin);

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
