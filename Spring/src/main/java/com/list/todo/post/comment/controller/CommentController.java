package com.list.todo.post.comment.controller;

import com.list.todo.auth.repository.UserRepository;
import com.list.todo.auth.security.CustomUserDetails;
import com.list.todo.post.comment.dto.CommentDto;
import com.list.todo.post.comment.dto.CommentResponse;
import com.list.todo.post.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/post/{boardId}")
    public ResponseEntity<?> getComments(@PathVariable Long boardId,
                                         @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        String loginId = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(commentService.getCommentList(boardId, page, size, loginId));
    }

//    @PutMapping("/{commentId}")
//    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
//                                                         @RequestBody CommentDto dto,
//                                                         Principal principal) {
//        String loginId = principal.getName();
//        return ResponseEntity.ok(commentService.updateComment(commentId, dto, loginId));
//    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Long commentId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        commentService.deleteComment(commentId, userId, isAdmin);

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
