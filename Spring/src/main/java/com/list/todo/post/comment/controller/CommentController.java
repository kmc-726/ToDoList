package com.list.todo.post.comment.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.comment.dto.CommentDto;
import com.list.todo.post.comment.dto.CommentResponse;
import com.list.todo.post.comment.service.CommentService;
import com.list.todo.post.shared.entity.LikeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentDto dto, Principal principal) {
        String loginId = principal.getName();
        return ResponseEntity.ok(commentService.createComment(dto, loginId));
    }

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(commentService.getCommentList(page, size));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
                                                         @RequestBody CommentDto dto,
                                                         Principal principal) {
        String loginId = principal.getName();
        return ResponseEntity.ok(commentService.updateComment(commentId, dto, loginId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Principal principal) {
        String loginId = principal.getName();
        commentService.deleteComment(commentId, loginId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요/싫어요 처리
//    @PostMapping("/{commentId}/like/{type}")
//    public ResponseEntity<?> handleLike(@PathVariable Long commentId,
//                                        @PathVariable String type,
//                                        @AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(commentService.handleLike(commentId, userDetails.getUsername(), LikeEntity.LikeType.valueOf(type.toUpperCase())));
//    }
}
