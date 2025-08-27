package com.list.todo.post.comment.controller;

import com.list.todo.post.comment.dto.CommentDto;
import com.list.todo.post.comment.dto.CommentResponse;
import com.list.todo.post.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // ✅ 1. 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentDto dto, Principal principal) {
        String loginId = principal.getName();
        return ResponseEntity.ok(commentService.createComment(dto, loginId));
    }

    // ✅ 2. 댓글 목록 조회 (게시글 기준)
    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Principal principal
    ) {
        String loginId = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(commentService.getComments(boardId, page, size, loginId));
    }

    // ✅ 3. 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDto dto,
            Principal principal
    ) {
        String loginId = principal.getName();
        return ResponseEntity.ok(commentService.updateComment(id, dto, loginId));
    }

    // ✅ 4. 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            Principal principal
    ) {
        String loginId = principal.getName();
        commentService.deleteComment(id, loginId);
        return ResponseEntity.noContent().build();
    }
}