package com.list.todo.post.board.controller;

import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.service.BoardService;
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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BoardRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.createBoard(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardService.getBoards(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody BoardRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.updateBoard(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        boardService.deleteBoard(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
